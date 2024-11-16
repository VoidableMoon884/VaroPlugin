package de.vmoon.varoPlugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Set;
import java.util.UUID;

public class scoreboardHandler {
    private final Scoreboard scoreboard;
    private final Objective objective;

    public scoreboardHandler() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) throw new IllegalStateException("ScoreboardManager ist nicht verfügbar.");
        this.scoreboard = manager.getNewScoreboard();

        this.objective = scoreboard.registerNewObjective("varoStats", "dummy", ChatColor.GREEN + "Varo Statistik");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void updateScoreboard(Set<UUID> registeredPlayers) {
        // Zählt nur Teams mit Spielern
        int activeTeams = (int) Bukkit.getScoreboardManager()
                .getMainScoreboard()
                .getTeams().stream()
                .filter(team -> !team.getEntries().isEmpty())  // Nur Teams mit Spielern zählen
                .count();

        // Setzt die Werte auf dem Scoreboard
        objective.getScore(ChatColor.YELLOW + "Lebende Spieler:").setScore(registeredPlayers.size());

        // Unterteilung hinzufügen (leere Zeile)
        objective.getScore(" --------------- ").setScore(registeredPlayers.size() + 1);

        // Teams mit Spielern anzeigen
        objective.getScore(ChatColor.YELLOW + "Teams:").setScore(activeTeams);
    }

    public void applyToPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void resetScoreboard() {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
    }
}
