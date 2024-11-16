package de.vmoon.varoPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.io.*;

public class registerCommand implements CommandExecutor, TabCompleter {
    private final Set<UUID> registeredPlayers = new HashSet<>();
    private final File dataFile;

    public registerCommand() {
        // Speicherpfad für registrierte Spieler
        this.dataFile = new File("plugins/VaroPlugin", "registered_players.dat");
        createPluginDirectory();  // Sicherstellen, dass der Ordner existiert
        loadRegisteredPlayers();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        if (!player.hasPermission("varo.register")) {
            player.sendMessage("§cDu hast keine Berechtigung, diesen Befehl auszuführen.");
            return false;
        }

        if (registeredPlayers.contains(player.getUniqueId())) {
            player.sendMessage("§cDu bist bereits registriert!");
        } else {
            registeredPlayers.add(player.getUniqueId());
            saveRegisteredPlayers();  // Jetzt ist es öffentlich und funktioniert hier
            player.sendMessage("§aDu hast dich erfolgreich registriert!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return List.of();
    }

    public boolean isPlayerRegistered(Player player) {
        return registeredPlayers.contains(player.getUniqueId());
    }

    public Set<UUID> getRegisteredPlayers() {
        return new HashSet<>(registeredPlayers);  // Gibt eine modifizierbare Kopie zurück
    }

    // Öffentlich zugänglich machen
    public void saveRegisteredPlayers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile, false))) {
            oos.writeObject(registeredPlayers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPluginDirectory() {
        File pluginDir = new File("plugins/VaroPlugin");
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
    }

    private void loadRegisteredPlayers() {
        if (dataFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                registeredPlayers.addAll((Set<UUID>) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
