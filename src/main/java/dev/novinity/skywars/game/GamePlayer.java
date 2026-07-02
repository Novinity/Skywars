package dev.novinity.skywars.game;

import dev.novinity.skywars.Skywars;
import org.bukkit.entity.Player;

public class GamePlayer {
    public Player player;

    public boolean alive = true;
    public int kills = 0;

    public GamePlayer(Player _player) {
        this.player = _player;
    }

    public void die() {
        alive = false;

        Skywars.getInstance().getGameManager().updatePlayerVisibility();
        Skywars.getInstance().getGameManager().registerDeath();
    }

    public void updateVisibility() {
        player.setAllowFlight(!alive);
        player.setFlying(!alive);
        for (GamePlayer gamePlayer : Skywars.getInstance().getGameManager().players) {
            if (alive || Skywars.getInstance().getGameManager().gameEnded) {
                gamePlayer.player.showPlayer(player);
                continue;
            }
            if (gamePlayer.alive) {
                gamePlayer.player.hidePlayer(player);
            } else {
                gamePlayer.player.showPlayer(player);
            }
        }
    }
}
