package dev.novinity.skywars.game;

import dev.novinity.skywars.Skywars;
import dev.novinity.skywars.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardHandler {
    static HashMap<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();

    public static void createScoreboard(GamePlayer _gamePlayer) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("skywars", "dummy", ChatColor.translateAlternateColorCodes('&', "&e&lSKYWARS"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        _setupObjective(objective, _gamePlayer);

        _gamePlayer.player.setScoreboard(scoreboard);
        scoreboards.put(_gamePlayer.player, scoreboard);
    }

    public static void updateScoreboard(GamePlayer _gamePlayer) {
        if (_gamePlayer.player == null) return;

        Scoreboard scoreboard = scoreboards.get(_gamePlayer.player);
        Objective objective = scoreboard.getObjective("skywars");

        _setupObjective(objective, _gamePlayer);
    }

    private static void _setupObjective(Objective objective, GamePlayer _gamePlayer) {
        if (objective == null || objective.getScoreboard() == null) {
            createScoreboard(_gamePlayer);
            _setupObjective(_gamePlayer.player.getScoreboard().getObjective("skywars"), _gamePlayer);
            return;
        }

        Score line11 = objective.getScore(ChatColor.WHITE + "Next Event:");
        line11.setScore(11);

        if (EventManager.nextEvent != null) setLine(objective.getScoreboard(), objective, "event", ChatColor.GREEN + EventManager.nextEvent.name() + " " + NumberUtils.formatTimeNoHours(EventManager.getTimeUntilNextEvent()), 10);
        else setLine(objective.getScoreboard(), objective, "event", ChatColor.GREEN + "NONE", 10);

        setLine(objective.getScoreboard(), objective, "players_left", ChatColor.WHITE + "Players left: " + ChatColor.GREEN + Skywars.getInstance().getGameManager().getLivingPlayers().size(), 8);

        setLine(objective.getScoreboard(), objective, "kills", ChatColor.WHITE + "Kills: " + ChatColor.GREEN + _gamePlayer.kills, 6);

        Score line1 = objective.getScore(ChatColor.YELLOW + "www.coswoodmc.com");
        line1.setScore(1);

//        objective.getScore(" ").setScore(13);
        objective.getScore("  ").setScore(12);
        objective.getScore("   ").setScore(9);
        objective.getScore("    ").setScore(7);
        objective.getScore("     ").setScore(5);
//        objective.getScore("      ").setScore(4);
//        objective.getScore("       ").setScore(3);
//        objective.getScore("        ").setScore(2);
    }

    public static void updateAllScoreboards() {
        for (Map.Entry<Player, Scoreboard> entry : scoreboards.entrySet()) {
            updateScoreboard(Skywars.getInstance().getGameManager().getGamePlayer(entry.getKey()));
        }
    }

    public static void deleteScoreboard(Player player) {
        Scoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) return;

        scoreboard.resetScores("skywars");
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboards.remove(player);
    }

    public static void deleteAllScoreboards() {
        while (!scoreboards.isEmpty()) {
            deleteScoreboard(scoreboards.keySet().iterator().next());
        }
    }

    private static void setLine(Scoreboard scoreboard, Objective objective, String id, String text, int score) {
        String entry = ChatColor.values()[score].toString() + ChatColor.RESET;

        Team team = scoreboard.getTeam(id);
        if (team == null) {
            team = scoreboard.registerNewTeam(id);
            team.addEntry(entry);
        }
        team.setPrefix(text.length() > 64 ? text.substring(0, 64) : text);

        objective.getScore(entry).setScore(score);
    }
}
