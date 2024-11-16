package de.vmoon.varoPlugin.commands;

import de.vmoon.varoPlugin.handler.scoreboardHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class unregisterCommand implements CommandExecutor {

    private final registerCommand registerCmd;  // Zugriff auf die registrierten Spieler
    private final scoreboardHandler scoreboardHandler;  // Zugriff auf das Scoreboard-Handler

    public unregisterCommand(registerCommand registerCmd, scoreboardHandler scoreboardHandler) {
        this.registerCmd = registerCmd;
        this.scoreboardHandler = scoreboardHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Überprüfen, ob der Sender ein Spieler ist
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        // Überprüfen, ob der Spieler registriert ist
        if (!registerCmd.isPlayerRegistered(player)) {
            player.sendMessage("§cDu bist noch nicht registriert!");
            return true;
        }

        // Entfernen des Spielers aus der registrierten Spieler-Liste
        UUID playerUUID = player.getUniqueId();
        registerCmd.getRegisteredPlayers().remove(playerUUID);

        // Speichern der aktualisierten registrierten Spieler
        registerCmd.saveRegisteredPlayers();

        // Scoreboard aktualisieren
        scoreboardHandler.updateScoreboard(registerCmd.getRegisteredPlayers());

        // Feedback an den Spieler
        player.sendMessage("§aDu wurdest erfolgreich abgemeldet!");

        return true;
    }

}
