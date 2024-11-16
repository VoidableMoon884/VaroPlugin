package de.vmoon.varoPlugin;

import de.vmoon.varoPlugin.commands.registerCommand;
import de.vmoon.varoPlugin.commands.unregisterCommand;
import de.vmoon.varoPlugin.handler.scoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaroPlugin extends JavaPlugin {

    private registerCommand registerCmd;
    private unregisterCommand unregisterCmd;
    private scoreboardHandler scoreboardHandler;

    @Override
    public void onEnable() {
        getLogger().info("VaroPlugin wird gestartet...");

        // Initialisiere Command und Handler
        this.registerCmd = new registerCommand();
        this.unregisterCmd = new unregisterCommand(registerCmd, new scoreboardHandler());
        this.scoreboardHandler = new scoreboardHandler();

        // Registriere die Commands
        getCommand("register").setExecutor(registerCmd);
        getCommand("unregister").setExecutor(unregisterCmd);  // Hier den /unregister Befehl registrieren

        // Starte regelmäßige Scoreboard-Aktualisierungen
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            // Aktivere Teams und registrierte Spieler holen
            scoreboardHandler.resetScoreboard();  // Setzt vorherige Punkte zurück
            scoreboardHandler.updateScoreboard(registerCmd.getRegisteredPlayers());  // Hier den Befehl verwenden

            // Anwenden des Scoreboards auf alle Online-Spieler
            Bukkit.getOnlinePlayers().forEach(scoreboardHandler::applyToPlayer);
        }, 20L, 20L); // Aktualisierung alle 1 Sekunde (20 Ticks)

        getLogger().info("VaroPlugin erfolgreich gestartet!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VaroPlugin wird gestoppt...");
    }
}
