package dev.jamieisgeek.tag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class Events implements Listener {

    Game game = Game.getGame();
    String prefix = game.getPrefix();
    ArrayList<UUID> queuedPlayers = game.getQueue();
    ArrayList<UUID> alivePlayers = game.getAlive();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if(game.getProgress() == true) {
            if(e.getDamager().getType().equals(EntityType.PLAYER)) {
                Player attacker = (Player) e.getDamager();

                if(e.getEntity().getType().equals(EntityType.PLAYER)) {
                    Player attacked = (Player) e.getEntity();

                    e.setCancelled(true);
                    if(attacker.getUniqueId().equals(game.getHunterID())) {
                        attacked.setGameMode(GameMode.SPECTATOR);
                        game.getAlive().remove(attacked.getUniqueId());

                        game.getQueue().forEach((UUID uuid) -> {
                            Player player = Bukkit.getPlayer(uuid);
                            player.sendMessage(prefix + ChatColor.RED + attacked.getName() + ChatColor.WHITE + " has been " + ChatColor.BOLD + "" + ChatColor.RED + "eliminated" + ChatColor.stripColor("") + ChatColor.WHITE + "!");
                        });


                        if(game.getAlive().size() == 0) {
                            game.endGame();
                        }
                    }
                }
            }
        } else {
            return;
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {

        if(queuedPlayers.contains(e.getPlayer().getUniqueId())) {
            queuedPlayers.remove(e.getPlayer().getUniqueId());
            if(alivePlayers.contains(e.getPlayer().getUniqueId())) {
                alivePlayers.remove(e.getPlayer().getUniqueId());

                queuedPlayers.forEach((UUID uuid) -> {
                    Player p = Bukkit.getPlayer(uuid);
                    p.sendMessage(prefix + e.getPlayer().getDisplayName() + " has left the game!");
                });

                if(alivePlayers.size() == 0) {
                    game.endGame();
                }

                if(e.getPlayer().getUniqueId() == game.getHunterID()) {
                    game.endGame();
                }
            }
        }

    }
}
