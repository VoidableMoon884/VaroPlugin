package de.vmoon.varoPlugin;

import de.vmoon.varoPlugin.commands.TeamCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class VaroPlugin extends JavaPlugin {
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<Player, Team> playerTeams = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("vteam").setExecutor(new TeamCommand(this));
        getCommand("vteam").setTabCompleter(new TeamCommand(this));
        loadPlayerTeams();
    }

    public void addTeam(String teamName, Team team) {
        teams.put(teamName, team);
    }

    public void removeTeam(String teamName) {
        teams.remove(teamName);
    }

    public Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void savePlayerTeam(Player player, Team team) {
        if (team == null) {
            playerTeams.remove(player);
        } else {
            playerTeams.put(player, team);
        }
    }

    public Team getPlayerTeam(Player player) {
        return playerTeams.get(player);
    }

    public void loadPlayerTeams() {
        // Hier könnten Teamzugehörigkeiten aus einer Datei oder Datenbank geladen werden
    }
}
