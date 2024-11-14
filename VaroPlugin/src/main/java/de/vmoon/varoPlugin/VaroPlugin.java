package de.vmoon.varoPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.vmoon.varoPlugin.commands.TeamCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class VaroPlugin extends JavaPlugin implements Listener {
    private final Map<String, Team> teams = new HashMap<>();  // Map für die Teams
    private final Map<Player, Team> playerTeams = new HashMap<>();  // Map für die Spieler-zu-Teams-Zuordnung
    private File teamsFile;  // Datei, in der die Teams gespeichert werden
    private File playerTeamsFile;  // Datei für die Spieler-Teams-Zuordnung

    @Override
    public void onEnable() {
        // Sicherstellen, dass der Ordner existiert
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        teamsFile = new File(getDataFolder(), "teams.json");
        playerTeamsFile = new File(getDataFolder(), "playerTeams.json");  // Neue Datei für Spieler-Teams

        // Datei erstellen, wenn sie noch nicht existiert
        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Fehler beim Erstellen der Datei teams.json: " + e.getMessage());
            }
        }
        if (!playerTeamsFile.exists()) {
            try {
                playerTeamsFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Fehler beim Erstellen der Datei playerTeams.json: " + e.getMessage());
            }
        }

        // Lade Teams und Spielerzugehörigkeiten
        loadTeams();
        loadPlayerTeams();  // Lade die Spieler-Teams-Zuordnung

        // Registriere die vteam-Befehle
        getCommand("vteam").setExecutor(new TeamCommand(this));
        getCommand("vteam").setTabCompleter(new TeamCommand(this));

        // Registriere Listener für Join und Quit Events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Speichern der Teams und Spielerzugehörigkeiten bei Deaktivierung des Plugins
        saveTeams();
        savePlayerTeams();  // Speichern der Spieler-Teams-Zuordnung
    }

    // Methode zum Hinzufügen eines neuen Teams
    public void addTeam(String teamName, Team team) {
        teams.put(teamName, team);
        saveTeams();  // Sofort speichern
    }

    // Methode zum Entfernen eines Teams
    public void removeTeam(String teamName) {
        teams.remove(teamName);
        saveTeams();  // Sofort speichern
    }

    // Methode zum Abrufen eines Teams anhand des Teamnamens
    public Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    // Methode zum Abrufen aller Teams
    public Map<String, Team> getTeams() {
        return teams;
    }

    // Methode zum Speichern eines Spielers und seines Teams
    public void savePlayerTeam(Player player, Team team) {
        if (team == null) {
            playerTeams.remove(player);  // Entferne den Spieler, wenn er keinem Team mehr angehört
        } else {
            playerTeams.put(player, team);  // Speichere den Spieler und sein Team
        }
        savePlayerTeams();  // Sofort speichern
    }

    // Methode zum Abrufen des Teams eines Spielers
    public Team getPlayerTeam(Player player) {
        return playerTeams.get(player);
    }

    // Methode zum Laden der Teams aus der JSON-Datei
    private void loadTeams() {
        if (!teamsFile.exists()) {
            return; // Wenn die Datei nicht existiert, gibt es nichts zu laden
        }

        try (FileReader reader = new FileReader(teamsFile)) {
            Gson gson = new Gson();
            Type teamMapType = new TypeToken<Map<String, Team>>(){}.getType();
            Map<String, Team> loadedTeams = gson.fromJson(reader, teamMapType);

            if (loadedTeams != null) {
                teams.putAll(loadedTeams);

                // Spieler zu den Teams zuweisen
                for (Map.Entry<String, Team> entry : loadedTeams.entrySet()) {
                    Team team = entry.getValue();
                    for (String playerName : team.getPlayerNames()) {
                        Player player = getServer().getPlayer(playerName);
                        if (player != null) {
                            // Spieler dem Team zuweisen
                            playerTeams.put(player, team);
                        }
                    }
                }
            }
        } catch (IOException e) {
            getLogger().warning("Fehler beim Laden der Teams: " + e.getMessage());
        }
    }

    // Methode zum Laden der Spieler-Teams-Zuordnung
    private void loadPlayerTeams() {
        if (!playerTeamsFile.exists()) {
            return; // Wenn die Datei nicht existiert, gibt es nichts zu laden
        }

        try (FileReader reader = new FileReader(playerTeamsFile)) {
            Gson gson = new Gson();
            Type playerTeamMapType = new TypeToken<Map<String, String>>(){}.getType();  // Map für Spieler -> Teamname
            Map<String, String> loadedPlayerTeams = gson.fromJson(reader, playerTeamMapType);

            if (loadedPlayerTeams != null) {
                for (Map.Entry<String, String> entry : loadedPlayerTeams.entrySet()) {
                    Player player = getServer().getPlayer(entry.getKey());
                    if (player != null) {
                        Team team = getTeam(entry.getValue());
                        if (team != null) {
                            playerTeams.put(player, team);
                        }
                    }
                }
            }
        } catch (IOException e) {
            getLogger().warning("Fehler beim Laden der Spieler-Teams: " + e.getMessage());
        }
    }

    // Methode zum Speichern der Teams in der JSON-Datei
    private void saveTeams() {
        try (FileWriter writer = new FileWriter(teamsFile)) {
            Gson gson = new Gson();
            gson.toJson(teams, writer);  // Speichere alle Teams in der Datei
        } catch (IOException e) {
            getLogger().warning("Fehler beim Speichern der Teams: " + e.getMessage());
        }
    }

    // Methode zum Speichern der Spieler-Teams-Zuordnung
    public void savePlayerTeams() {
        try (FileWriter writer = new FileWriter(playerTeamsFile)) {
            Gson gson = new Gson();
            Map<String, String> playerTeamMap = new HashMap<>();
            for (Map.Entry<Player, Team> entry : playerTeams.entrySet()) {
                playerTeamMap.put(entry.getKey().getName(), entry.getValue().getName());
            }
            gson.toJson(playerTeamMap, writer);  // Speichern der Zuordnung Spieler -> Teamname
        } catch (IOException e) {
            getLogger().warning("Fehler beim Speichern der Spieler-Teams: " + e.getMessage());
        }
    }

    // Event-Handler für Spieler-Join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = playerTeams.get(player);

        if (team != null) {
            // Weisen Sie den Spieler seinem Team zu
            playerTeams.put(player, team);
        }
    }

    // Event-Handler für Spieler-Quit
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Team team = playerTeams.get(player);

        if (team != null) {
            // Speichern der Spieler-Team-Zuordnung beim Verlassen des Servers
            savePlayerTeam(player, team);
        }
    }
}
