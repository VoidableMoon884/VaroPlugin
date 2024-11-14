package de.vmoon.varoPlugin;

import de.vmoon.varoPlugin.commands.TeamCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class VaroPlugin extends JavaPlugin {
    private final Map<String, Team> teams = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("vteam").setExecutor(new TeamCommand(this));
    }

    // Gibt das Team eines Spielers zurück
    public Team getPlayerTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.getPlayers().contains(player)) {
                return team;  // Rückgabe des Teams, wenn der Spieler Mitglied ist
            }
        }
        return null;  // Falls der Spieler keinem Team angehört
    }

    // Gibt eine Map mit allen Teams zurück
    public Map<String, Team> getTeams() {
        return teams;
    }

    // Fügt ein Team hinzu
    public void addTeam(String name, Team team) {
        teams.put(name, team);
    }

    // Entfernt ein Team
    public void removeTeam(String name) {
        teams.remove(name);
    }
}
