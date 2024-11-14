package de.vmoon.varoPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class Team {
    private String name;
    private ChatColor color;
    private Set<String> playerNames;  // Speichern der Spielernamen als Strings, um sie in JSON zu serialisieren

    public Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;
        this.playerNames = new HashSet<>();
    }

    // Konstruktor für die Deserialisierung aus JSON
    public Team(String name, ChatColor color, Set<String> playerNames) {
        this.name = name;
        this.color = color;
        this.playerNames = playerNames != null ? playerNames : new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Set<String> getPlayerNames() {
        return playerNames;
    }

    public void addPlayer(Player player) {
        playerNames.add(player.getName());
    }

    public void removePlayer(Player player) {
        playerNames.remove(player.getName());
    }

    // Rückgabe der Player-Objekte anhand der gespeicherten Spielernamen
    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        for (String playerName : playerNames) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    // Setter-Methoden zum Ändern des Teamnamens und der Teamfarbe
    public void setName(String name) {
        this.name = name;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }
}
