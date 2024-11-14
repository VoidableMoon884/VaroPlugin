package de.vmoon.varoPlugin.commands;

import de.vmoon.varoPlugin.Team;
import de.vmoon.varoPlugin.VaroPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private final VaroPlugin plugin;

    public TeamCommand(VaroPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Benutze: /vteam [create|delete|join|leave|modify|list]");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "modify":
                handleModify(player, args);
                break;
            case "list":
                handleList(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unbekannter Unterbefehl.");
                break;
        }

        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("varo.team.create")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, ein Team zu erstellen.");
            return;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Benutze: /vteam create [teamname] [farbe]");
            return;
        }

        String teamName = args[1];
        ChatColor color;
        try {
            color = ChatColor.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Ungültige Farbe. Verwende z.B. RED, GREEN, BLUE.");
            return;
        }

        if (plugin.getTeams().containsKey(teamName)) {
            player.sendMessage(ChatColor.RED + "Ein Team mit diesem Namen existiert bereits.");
            return;
        }

        Team team = new Team(teamName, color);
        plugin.addTeam(teamName, team);
        player.sendMessage(ChatColor.GREEN + "Team " + color + teamName + ChatColor.GREEN + " wurde erstellt.");
    }

    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("varo.team.delete")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, ein Team zu löschen.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Benutze: /vteam delete [teamname]");
            return;
        }

        String teamName = args[1];
        Team team = plugin.getTeams().get(teamName);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "Dieses Team existiert nicht.");
            return;
        }

        plugin.removeTeam(teamName);
        player.sendMessage(ChatColor.GREEN + "Team " + teamName + " wurde gelöscht.");
    }

    private void handleJoin(Player player, String[] args) {
        if (!player.hasPermission("varo.team.join")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, einem Team beizutreten.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Benutze: /vteam join [teamname]");
            return;
        }

        String teamName = args[1];
        Team team = plugin.getTeams().get(teamName);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "Dieses Team existiert nicht.");
            return;
        }

        team.addPlayer(player);
        player.setPlayerListName(team.getColor() + "[" + teamName + "] " + ChatColor.RESET + player.getName());
        player.setDisplayName(team.getColor() + "[" + teamName + "] " + ChatColor.RESET + player.getName());
        player.sendMessage(ChatColor.GREEN + "Du bist dem Team " + team.getColor() + teamName + ChatColor.GREEN + " beigetreten.");

        plugin.savePlayerTeam(player, team);  // Speichern der Teamzugehörigkeit des Spielers
    }

    private void handleLeave(Player player) {
        if (!player.hasPermission("varo.team.leave")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, ein Team zu verlassen.");
            return;
        }

        Team team = plugin.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "Du bist in keinem Team.");
            return;
        }

        team.removePlayer(player);
        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
        player.sendMessage(ChatColor.GREEN + "Du hast das Team " + team.getColor() + team.getName() + ChatColor.GREEN + " verlassen.");

        plugin.savePlayerTeam(player, null);  // Entfernen der Teamzugehörigkeit des Spielers
    }

    private void handleModify(Player player, String[] args) {
        if (!player.hasPermission("varo.team.modify")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, ein Team zu ändern.");
            return;
        }

        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Benutze: /vteam modify [teamname] [neuerTeamname] [neueFarbe]");
            return;
        }

        String teamName = args[1];
        String newTeamName = args[2];
        ChatColor newColor;
        try {
            newColor = ChatColor.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Ungültige Farbe. Verwende z.B. RED, GREEN, BLUE.");
            return;
        }

        Team team = plugin.getTeams().get(teamName);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "Dieses Team existiert nicht.");
            return;
        }

        team.setName(newTeamName);
        team.setColor(newColor);
        plugin.removeTeam(teamName);
        plugin.addTeam(newTeamName, team);

        player.sendMessage(ChatColor.GREEN + "Team " + teamName + " wurde in " + newColor + newTeamName + ChatColor.GREEN + " umbenannt.");
    }

    private void handleList(Player player) {
        if (plugin.getTeams().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Es gibt keine Teams.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Verfügbare Teams:");
        for (String teamName : plugin.getTeams().keySet()) {
            Team team = plugin.getTeams().get(teamName);
            player.sendMessage(team.getColor() + teamName + ChatColor.RESET);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCommand : new String[]{"create", "delete", "join", "leave", "modify", "list"}) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    suggestions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("modify")) {
                for (String teamName : plugin.getTeams().keySet()) {
                    if (teamName.startsWith(args[1].toLowerCase())) {
                        suggestions.add(teamName);
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("modify")) {
            for (ChatColor color : ChatColor.values()) {
                if (color.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                    suggestions.add(color.name());
                }
            }
        }

        return suggestions;
    }
}
