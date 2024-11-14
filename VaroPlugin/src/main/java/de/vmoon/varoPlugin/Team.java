package de.vmoon.varoPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Team {
    private String name;
    private ChatColor color;
    private final Set<Player> players = new HashSet<>();

    public Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }
}
