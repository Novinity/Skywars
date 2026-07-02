package dev.novinity.skywars.game;

import org.bukkit.Location;

import java.util.ArrayList;

public class Team {
    public ArrayList<GamePlayer> players = new ArrayList<>();
    public Location spawnpoint;

    public ArrayList<GamePlayer> getAlivePlayers() {
        ArrayList<GamePlayer> _players = new ArrayList<>();
        for (GamePlayer _gamePlayer : this.players) {
            if (_gamePlayer.alive) _players.add(_gamePlayer);
        }
        return _players;
    }
}
