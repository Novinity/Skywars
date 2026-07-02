package dev.novinity.skywars.game;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.utils.LocationUtils;
import dev.novinity.skywars.utils.SoundUtils;
import dev.novinity.skywars.utils.TitleUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class GameManager {
    private int INTERMISSION_TIME = 30;
    private int MINIMUM_PLAYERS = 2;
    private int MAXIMUM_PLAYERS = 16;

    private World world;
    private ConfigurationSection mapConfig;

    public ArrayList<GamePlayer> players = new ArrayList<>();
    public ArrayList<Team> teams = new ArrayList<>();

    public boolean setup = false;
    public boolean gameStarted = false;
    public boolean gameEnded = false;

    private int _timer;

    public void setupGame(World _world) {
        this.world = _world;

        mapConfig = Skywars.getMapsConfig().getConfigurationSection(world.getName());
        MINIMUM_PLAYERS = Skywars.getInstance().getConfig().getInt("minimumPlayers", 2);
        MAXIMUM_PLAYERS = Skywars.getInstance().getConfig().getInt("minimumPlayers", 16);
        INTERMISSION_TIME = Skywars.getInstance().getConfig().getInt("intermissionTime", 30);

        populateChests();
        _runStartTimer();

        EventManager.init();

        setup = true;
    }

    public void populateChests() {
        Set<String> chestKeys = mapConfig.getConfigurationSection("chests").getKeys(false);

        for (String chest : chestKeys) {
            ConfigurationSection chestSection = mapConfig.getConfigurationSection("chests." + chest);
            if (chestSection == null) continue;

            String typeString = chestSection.getString("type");
            Location location = chestSection.getLocation("location");
            if (typeString == null || typeString.isEmpty() || !ChestManager.ChestType.hasType(typeString) || location == null) continue;
            ChestManager.ChestType type = ChestManager.ChestType.valueOf(chestSection.getString("type"));

            ChestManager.populateChest(location, type);
        }
    }

    private void _runStartTimer() {
        _timer = INTERMISSION_TIME + 1;
        Bukkit.getScheduler().runTaskTimer(Skywars.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (gameStarted) {
                    return;
                }
                if (players.size() < MINIMUM_PLAYERS) {
                    if (_timer < INTERMISSION_TIME) {
                        TitleUtils.SendBroadcast("&cNot enough players to start the game.");
                        SoundUtils.PlaySoundForAll(Sound.UI_BUTTON_CLICK, 1F, 1F);
                    }
                    _timer = INTERMISSION_TIME + 1;
                    return;
                }

                _timer = Math.clamp(_timer - 1, 0, INTERMISSION_TIME);
                if (_timer <= 0) {
                    TitleUtils.ShowTitleForAll("&bFIGHT!", null, null, null, 20);
                    SoundUtils.PlaySoundForAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                    startGame();
                } else {
                    if (_timer % 30 == 0 || _timer % 20 == 0 || _timer % 10 == 0) {
                        TitleUtils.SendBroadcast("&aGame starting in " + _timer + " seconds.");
                        SoundUtils.PlaySoundForAll(Sound.UI_BUTTON_CLICK, 1F, 1F);
                    }
                    if (_timer <= 3) {
                        TitleUtils.ShowTitleForAll("&b" + _timer, null, null, null, null);
                        SoundUtils.PlaySoundForAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.7F);
                    }
                }
            }
        }, 0L, 20L);
    }

    public void setupPlayer(Player _player, @Nullable Team _team) {
        if (!setup) return;

        Random random = new Random();
        Location spawnpoint;

        if (_team != null) {
            spawnpoint = _team.spawnpoint;
        } else {
            Set<String> spawnpoints = mapConfig.getConfigurationSection("spawnpoints").getKeys(false);
            ArrayList<Location> availableSpawnpoints = new ArrayList<>();

            for (String sp : spawnpoints) {
                ConfigurationSection _spawnPoint = mapConfig.getConfigurationSection("spawnpoints" + "." + sp);
                if (_spawnPoint != null) {
                    Location _loc = _getSpawnpointFromCage(_spawnPoint);
                    if (!_isSpawnpointTaken(_loc) && !availableSpawnpoints.contains(_loc)) {
                        availableSpawnpoints.add(_loc);
                    }
                }
            }

            spawnpoint = availableSpawnpoints.get(random.nextInt(availableSpawnpoints.size()));
        }

        GamePlayer gamePlayer = new GamePlayer(_player);
        players.add(gamePlayer);
        ScoreboardHandler.createScoreboard(gamePlayer);

        if (_team == null) {
            _team = new Team();
        }
        _team.players.add(gamePlayer);
        if (!teams.contains(_team)) {
            teams.add(_team);
        }

        if (spawnpoint == null || players.size() > MAXIMUM_PLAYERS) {
            gamePlayer.alive = false;
            gamePlayer.updateVisibility();
        } else {
            if (_team.spawnpoint == null) {
                _team.spawnpoint = spawnpoint;
            }

            _player.teleport(_team.spawnpoint);
            _player.setGameMode(GameMode.SURVIVAL);
            _player.setHealth(_player.getMaxHealth());
            _player.setFoodLevel(20);
            _player.setSaturation(20);
            _player.getInventory().clear();
        }

        ScoreboardHandler.updateAllScoreboards();
    }

    public void deinitializePlayer(Player _player) {
        GamePlayer gamePlayer = getGamePlayer(_player);
        Team team = getTeamWithPlayer(gamePlayer);
        if (team != null) {
            team.players.remove(gamePlayer);
            if (team.players.isEmpty()) {
                teams.remove(team);
            }
        }
        if (gamePlayer != null) {
            gamePlayer.die();
        }
        players.remove(gamePlayer);

        ScoreboardHandler.updateAllScoreboards();
    }

    public void startGame() {
        Set<String> spawnpoints = mapConfig.getConfigurationSection("spawnpoints").getKeys(false);

        for (String spawnpoint : spawnpoints) {
            ConfigurationSection _spawnPoint = mapConfig.getConfigurationSection("spawnpoints" + "." + spawnpoint);
            if (_spawnPoint == null) {
                continue;
            }

            Location pos1 = _spawnPoint.getLocation("pos1");
            Location pos2 = _spawnPoint.getLocation("pos2");
            if (pos1 == null || pos2 == null) {
                continue;
            }

            for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
                for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {
                    for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }
        EventManager.beginEvents();

        gameStarted = true;
        ScoreboardHandler.updateAllScoreboards();
    }

    public GamePlayer getGamePlayer(Player _player) {
        for (GamePlayer _gamePlayer : players) {
            if (_gamePlayer.player == _player) {
                return _gamePlayer;
            }
        }
        return null;
    }

    public Team getTeamWithPlayer(GamePlayer _gamePlayer) {
        for (Team _team : teams) {
            if (_team.players.contains(_gamePlayer)) {
                return _team;
            }
        }
        return null;
    }

    private boolean _isSpawnpointTaken(Location _location) {
        for (Team _team : teams) {
            if (LocationUtils.isLocationPosSame(_location, _team.spawnpoint)) {
                return true;
            }
        }
        return false;
    }

    private Location _getSpawnpointFromCage(ConfigurationSection _cageConfig) {
        Location pos1 = _cageConfig.getLocation("pos1");
        Location pos2 = _cageConfig.getLocation("pos2");
        if (pos1 == null || pos2 == null) {
            return null;
        }

        double centerX = (double) (pos1.getBlockX() + pos2.getBlockX()) / 2;
        double centerY = (double) (pos1.getBlockY() + pos2.getBlockY()) / 2;
        double centerZ = (double) (pos1.getBlockZ() + pos2.getBlockZ()) / 2;

        return new Location(world, centerX + 0.5, centerY, centerZ + 0.5);
    }

    public void updatePlayerVisibility() {
        for (GamePlayer _gamePlayer : players) {
            _gamePlayer.updateVisibility();
        }
    }

    public void registerDeath() {
        if (!setup || !gameStarted) return;

        ArrayList<Team> livingTeams = new ArrayList<>();
        for (Team _team : teams) {
            if (!_team.getAlivePlayers().isEmpty()) {
                livingTeams.add(_team);
            }
        }

        ScoreboardHandler.updateAllScoreboards();

        if (livingTeams.size() == 1) {
            _endGame();
        }
    }

    private void _endGame() {
        ArrayList<Team> livingTeams = new ArrayList<>();
        for (Team _team : teams) {
            if (!_team.getAlivePlayers().isEmpty()) {
                livingTeams.add(_team);
            }
        }

        gameEnded = true;
        updatePlayerVisibility();

        Team winningTeam = livingTeams.getFirst();

        StringBuilder endMessage = new StringBuilder();
        endMessage.append("&a&l------------------------------\n");
        endMessage.append("&f&lSkyWars\n\n");

        if (!winningTeam.players.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (GamePlayer _teamPlayer :  winningTeam.players) {
                joiner.add(_teamPlayer.player.getDisplayName());
            }
            endMessage.append("&eWinner - ").append(joiner).append("\n\n");
        }

        ArrayList<GamePlayer> topKillers = getTopKillers();
        if (!topKillers.isEmpty()) endMessage.append("&e&l#1 Killer &7- ").append(topKillers.get(0).player.getDisplayName()).append(" &7- ").append(topKillers.get(0).kills).append("\n");
        if (topKillers.size() >= 2) endMessage.append("&6&l#2 Killer &7- ").append(topKillers.get(1).player.getDisplayName()).append(" &7- ").append(topKillers.get(1).kills).append("\n");
        if (topKillers.size() >= 3) endMessage.append("&c&l#3 Killer &7- ").append(topKillers.get(2).player.getDisplayName()).append(" &7- ").append(topKillers.get(2).kills).append("\n");

        endMessage.append("&a&l------------------------------");

        TitleUtils.SendBroadcast(endMessage.toString());

        for (GamePlayer _gamePlayer : winningTeam.players) {
            _gamePlayer.player.setVelocity(new Vector(0, 1, 0));
            _gamePlayer.player.setAllowFlight(true);
            _gamePlayer.player.setFlying(true);

            final int[] loops = {0};
            Bukkit.getScheduler().runTaskTimer(Skywars.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (loops[0] >= 20) return;

                    _gamePlayer.player.getWorld().spawnEntity(_gamePlayer.player.getLocation(), EntityType.FIREWORK_ROCKET);

                    loops[0]++;
                }
            }, 0L, 5L);
        }

        Bukkit.getScheduler().runTaskLater(Skywars.getInstance(), new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 20L*10);
    }

    public World getWorld() {
        return world;
    }

    private ArrayList<GamePlayer> getTopKillers() {
        ArrayList<GamePlayer> killers = new ArrayList<>(players);
        killers.sort(Comparator.comparingInt(o -> o.kills));

        ArrayList<GamePlayer> topKillers = new ArrayList<>();
        if (killers.size() <= 3) {
            for (GamePlayer _gamePlayer : killers) {
                if (_gamePlayer.kills == 0) continue;
                topKillers.add(_gamePlayer);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                if (killers.get(i).kills == 0) continue;
                topKillers.add(killers.get(i));
            }
        }
        return topKillers;
    }

    public ArrayList<GamePlayer> getLivingPlayers() {
        ArrayList<GamePlayer> livingPlayers = new ArrayList<>();
        for (GamePlayer _gamePlayer : players) {
            if (_gamePlayer.alive) livingPlayers.add(_gamePlayer);
        }
        return livingPlayers;
    }

    public void reset() {
        world = null;
        mapConfig = null;
        players.clear();
        teams.clear();

        setup = false;
        gameStarted = false;
        gameEnded = false;

        _timer = 0;

        Bukkit.getScheduler().cancelTasks(Skywars.getInstance());

        ScoreboardHandler.deleteAllScoreboards();
        EventManager.reset();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Game ended");
        }
    }
}
