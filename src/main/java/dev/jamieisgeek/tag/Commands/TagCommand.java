package dev.jamieisgeek.tag.Commands;

import dev.jamieisgeek.tag.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagCommand implements CommandExecutor {
    private final String prefix = ChatColor.WHITE + "[" + ChatColor.DARK_GREEN + ChatColor.BOLD + "ParkourTag" + ChatColor.RESET + ChatColor.WHITE + "] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;


            if(args[0].equalsIgnoreCase("start")) {

                if(p.hasPermission("tag.start")) {
                    Game game = Game.getGame();
                    game.beginGame();
                } else {
                    p.sendMessage(prefix + "Missing Permission: tag.start");

                }

            } else if (args[0].equalsIgnoreCase("join")) {

                if(p.hasPermission("tag.join")) {
                    Game game = Game.getGame();
                    game.joinGame(p);
                } else {
                    p.sendMessage(prefix + "Missing Permission: tag.join");
                }

            } else if (args[0].equalsIgnoreCase("end")) {

                if(p.hasPermission("tag.end")) {
                    Game game = Game.getGame();
                    game.endGame();
                } else {
                    p.sendMessage(prefix + "Missing Permission: tag.end");
                }

            } else if (args[0].equalsIgnoreCase("leave")) {

                if(p.hasPermission("tag.leave")) {
                    Game game = Game.getGame();
                    game.leaveQueue(p);
                } else {
                    p.sendMessage(prefix + "Missing Permission: tag.leave");
                }
            } else if (args[0].equalsIgnoreCase("list")) {

                if(p.hasPermission("tag.list")) {
                    Game game = Game.getGame();
                    game.listQueue(p);
                } else {
                    p.sendMessage(prefix + "Missing Permission: tag.list");
                }
            }


        } else {
            Bukkit.getLogger().warning("You must run this command in-game!");
        }
        return true;
    }
}
