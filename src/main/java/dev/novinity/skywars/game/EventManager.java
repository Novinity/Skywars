package dev.novinity.skywars.game;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.utils.SoundUtils;
import dev.novinity.skywars.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
    public enum EventType {
        REFILL,
        DOOM
    }

    public static EventType nextEvent;
    private static int timeUntilNextEvent = 0;
    private static int eventIndex = -1;

    private static ArrayList<HashMap<EventType, Integer>> eventSequence;

    public static void init() {
        eventSequence = new ArrayList<>() {{
            add(new HashMap<>(Map.of(EventType.REFILL, 10)));
            add(new HashMap<>(Map.of(EventType.REFILL, 10)));
            add(new HashMap<>(Map.of(EventType.DOOM, 10)));
        }};
    }

    public static void beginEvents() {
        Bukkit.getScheduler().runTaskTimer(Skywars.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (timeUntilNextEvent == 0) {
                    if (eventIndex > -1) {
                        _runEvent(nextEvent);
                    }
                    if (eventSequence.size() > eventIndex + 1) {
                        eventIndex++;
                        nextEvent = eventSequence.get(eventIndex).keySet().iterator().next();
                        timeUntilNextEvent = eventSequence.get(eventIndex).get(nextEvent);
                    } else {
                        timeUntilNextEvent = -1;
                        nextEvent = null;
                        ScoreboardHandler.updateAllScoreboards();
                        return;
                    }
                }
                if (timeUntilNextEvent < 0) return;

                timeUntilNextEvent--;
                ScoreboardHandler.updateAllScoreboards();
            }
        }, 0L, 20L);
    }

    private static void _runEvent(EventType event) {
        switch (event) {
            case REFILL:
                Skywars.getInstance().getGameManager().populateChests();
                TitleUtils.SendBroadcast("&aAll chests have been refilled.");
                SoundUtils.PlaySoundForAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                break;
            case DOOM:
                Skywars.getInstance().getGameManager().getWorld().getWorldBorder().setSize(136);
                Skywars.getInstance().getGameManager().getWorld().getWorldBorder().setSize(32, 120);
                TitleUtils.SendBroadcast("&aThe border is now closing.");
                break;
        }
    }

    public static void reset() {
        nextEvent = null;
        timeUntilNextEvent = 0;
        eventIndex = 0;
        eventSequence.clear();
    }

    public static int getTimeUntilNextEvent() {
        return timeUntilNextEvent;
    }
}
