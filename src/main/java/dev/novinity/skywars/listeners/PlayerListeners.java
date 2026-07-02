package dev.novinity.skywars.listeners;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.game.GamePlayer;
import dev.novinity.skywars.game.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class PlayerListeners implements Listener {

    ArrayList<Player> canTakeFallDamage = new ArrayList<>();

    private boolean _canAction(@Nullable GamePlayer player) {
        if (player == null || !Skywars.getInstance().getGameManager().setup) return true;
        return Skywars.getInstance().getGameManager().gameStarted && player.alive && !Skywars.getInstance().getGameManager().gameEnded;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Skywars.getInstance().getGameManager().setupPlayer(player, null);
        ScoreboardHandler.updateAllScoreboards();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ScoreboardHandler.deleteScoreboard(player);
        Skywars.getInstance().getGameManager().deinitializePlayer(player);
        ScoreboardHandler.updateAllScoreboards();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player);
        if (gamePlayer == null) return;

        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (damageEvent != null) {
            switch (damageEvent.getCause()) {
                case ENTITY_ATTACK -> {
                    if (player.getKiller() != null) {
                        event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getDisplayName() + " &ewas killed by&r " + player.getKiller().getDisplayName() + "&e."));
                    } else {
                        event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getDisplayName() + " &edied."));
                    }
                }
                case FALL -> {
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getDisplayName() + " &efell to their death."));
                }
                case LAVA -> {
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getDisplayName() + " &etook a swim in lava."));
                }
                default -> {
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', event.getEntity().getDisplayName() + " &edied."));
                }
            }
        }

        if (player.getKiller() != null) {
            GamePlayer _gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player.getKiller());
            if (_gamePlayer != null) {
                _gamePlayer.kills++;
            }
        }

        Bukkit.getScheduler().runTaskLater(Skywars.getInstance(), gamePlayer::die, 1L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player);
        if (gamePlayer == null) return;

        Skywars.getInstance().getGameManager().updatePlayerVisibility();
        event.setRespawnLocation(new Location(Skywars.getInstance().getGameManager().getWorld(), 0, 100, 0));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(event.getPlayer());
        if (!_canAction(gamePlayer)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(event.getPlayer());
        if (!_canAction(gamePlayer)) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation().add(0.5, 0.0, 0.5), EntityType.TNT);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player);
            if (!_canAction(gamePlayer)) {
                event.setCancelled(true);
            }
            if (!canTakeFallDamage.contains(player) && event.getDamageSource().getDamageType() == DamageType.FALL) {
                event.setCancelled(true);
                canTakeFallDamage.add(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player);
            if (!_canAction(gamePlayer)) {
                event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Player otherPlayer) {
            GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(otherPlayer);
            if (!_canAction(gamePlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player player) {
            GamePlayer gamePlayer = Skywars.getInstance().getGameManager().getGamePlayer(player);
            if (!_canAction(gamePlayer)) {
                event.setCancelled(true);
            }
        }
    }
}
