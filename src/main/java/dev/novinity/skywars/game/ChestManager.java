package dev.novinity.skywars.game;

import dev.novinity.skywars.Skywars;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class ChestManager {
    public enum ChestType {
        ISLAND,
        SIDE,
        CENTER;

        public static boolean hasType(String s) {
            for (ChestType t : values()) {
                if (t.name().equalsIgnoreCase(s)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void populateChest(Location location, ChestType chestType) {
        if (location.getBlock().getState() instanceof Chest chest) {
            chest.getBlockInventory().clear();
            ArrayList<ItemStack> loot = generateLoot(chestType.name(), 3, 7);
            ArrayList<Integer> availableSlots = new ArrayList<>();
            for (int i = 0; i < chest.getBlockInventory().getSize(); i++) {
                availableSlots.add(i);
            }

            Random rand = new Random();

            for (ItemStack item : loot) {
                Integer slot = rand.nextInt(availableSlots.size());
                chest.getBlockInventory().setItem(slot, item);
                availableSlots.remove(slot);
            }
        }
    }

    private static HashMap<String, Double> getLootTable(String chestType) {
        HashMap<String, Double> lootTable = new HashMap<>();

        ConfigurationSection configSection = Skywars.getLootTablesConfig().getConfigurationSection(chestType.toUpperCase());
        if (configSection == null) return null;

        Set<String> keys = configSection.getKeys(false);
        for (String key : keys) {
            ConfigurationSection lootInfo = configSection.getConfigurationSection(key);
            if (lootInfo == null) continue;

            double weight = lootInfo.getDouble("weight");
            if (weight <= 0) continue;

            lootTable.put(key, weight);
        }

        return lootTable;
    }

    private static ArrayList<ItemStack> generateLoot(String chestType, int minStacks, int maxStacks) {
        HashMap<String, Double> lootTable = getLootTable(chestType);
        if (lootTable == null || lootTable.isEmpty()) return new ArrayList<>();
        ConfigurationSection configSection =  Skywars.getLootTablesConfig().getConfigurationSection(chestType.toUpperCase());

        Random rand = new Random();
        int stackCount = rand.nextInt(minStacks, maxStacks + 1);
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < stackCount; i++) {
            HashMap<String, ItemStack> table = new HashMap<>();
            double r = rand.nextDouble();

            for (String key : lootTable.keySet()) {
                if (r <= lootTable.get(key)) {
                    ItemStack stack = configSection.getItemStack(key + ".item");
                    if (stack == null) continue;
                    table.put(key, stack);
                }
            }

            if (table.isEmpty()) continue;

            String pickedKey = table.keySet().toArray()[rand.nextInt(table.keySet().size())].toString();
            ItemStack picked = table.get(pickedKey);
            items.add(picked);
        }

        return items;
    }
}
