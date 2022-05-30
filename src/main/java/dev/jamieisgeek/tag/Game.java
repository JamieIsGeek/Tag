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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Game implements Listener {

    private Integer startTimer = 30;
    private Integer roundTimer = 300;
    private final ArrayList<UUID> queuedPlayers = new ArrayList<>();
    private final ArrayList<UUID> alivePlayers = new ArrayList<>();
    private HashMap<String, String> roles = new HashMap<>();
    private final String prefix = ChatColor.WHITE + "[" + ChatColor.DARK_GREEN + ChatColor.BOLD + "ParkourTag" + ChatColor.RESET + ChatColor.WHITE + "] ";
    private Player hunter;
    private UUID hunterID;
    private Plugin plugin = Tag.getPlugin(Tag.class);
    private boolean inProgress = false;
    private Integer beginTimer = 6;
    public static Game game;

    private String shortInteger(int roundTimer) {
        String string = "";
        int minutes = 0;
        int seconds = 0;
        if (roundTimer / 60 / 60 / 24 >= 1) {
            roundTimer -= roundTimer / 60 / 60 / 24 * 60 * 60 * 24;
        }
        if (roundTimer / 60 >= 1) {
            minutes = roundTimer / 60;
            roundTimer -= roundTimer / 60 * 60;
        }
        if (roundTimer >= 1)
            seconds = roundTimer;
        if (minutes <= 9) {
            string = String.valueOf(string) + "0" + minutes + ":";
        } else {
            string = String.valueOf(string) + minutes + ":";
        }
        if (seconds <= 9) {
            string = String.valueOf(string) + "0" + seconds;
        } else {
            string = String.valueOf(string) + seconds;
        }
        return string;
    }




    public void joinGame(Player p) {
        UUID playerUUID = p.getUniqueId();

        if(inProgress) {
            p.sendMessage(prefix + "There is a game already in-progress. Please wait until that has finished!");
            return;
        }

        if(queuedPlayers.contains(playerUUID)) {
            p.sendMessage(prefix + "You are already in the queue for this game!");
            return;
        }

        queuedPlayers.add(playerUUID);
        alivePlayers.add(playerUUID);

        queuedPlayers.forEach((UUID pUUID) -> {
            Player player = Bukkit.getPlayer(pUUID);
            p.sendMessage(prefix + player.getName() + " has joined the queue!");
        });

        if(queuedPlayers.size() >= 3) {
            beginingCountdown();
        }
    }



    public void beginGame() {
        if(queuedPlayers.size() < 3) {
            if(queuedPlayers.size() == 0) {
                return;
            } else {
                queuedPlayers.forEach((UUID pUUID) -> {
                    Player player = Bukkit.getPlayer(pUUID);
                    player.sendMessage(prefix + "Not enough players to start the game!");
                });
            }
        }
        inProgress = true;
        final int randomInt = new Random().nextInt(queuedPlayers.size());
        hunterID = queuedPlayers.get(randomInt);
        hunter = Bukkit.getPlayer(hunterID);
        alivePlayers.remove(hunterID);

        queuedPlayers.forEach((UUID uuid) -> {
            if(uuid.equals(hunterID)) {
                roles.put("hunter", String.valueOf(uuid));
            } else {
                roles.put(String.valueOf(uuid), "runner");
            }
        });

        queuedPlayers.forEach((UUID uuid) -> {
            Player player = Bukkit.getPlayer(uuid);

            createScoreboard(uuid, player);

            player.sendMessage(prefix + "Welcome to Parkour Tag!");
            player.sendMessage(prefix + "This gamemode is simple, run from the hunter while doing parkour!");
            player.sendMessage(prefix + "A random player has been declared as the 'Hunter' you must run away from them and not get hit!");
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
            }
        }, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                beginTimer--;

                if(beginTimer < 6) {
                    queuedPlayers.forEach((UUID uuid) -> {
                        Player player = Bukkit.getPlayer(uuid);
                        player.sendMessage(prefix + beginTimer);
                    });

                    if(beginTimer == 0) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);

        updateScoreboard();
    }




    public void endGame() {
        inProgress = false;

        queuedPlayers.forEach((UUID uuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if(alivePlayers.size() > 0) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                player.sendMessage(prefix + "Game Over!");

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 30L);
                player.sendMessage(prefix + "Runners Win!");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 20L);
            } else {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                player.sendMessage(prefix + "Game Over!");

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 20L);

                player.sendMessage(prefix + "The Hunter wins!");
            }
        });

        queuedPlayers.clear();
        alivePlayers.clear();
        roles.clear();
    }



    private void updateScoreboard() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    roundTimer--;
                    if(roundTimer == 0) {
                        this.cancel();
                        endGame();
                    }

                    queuedPlayers.forEach((UUID uuid) -> {
                        Player p = Bukkit.getPlayer(uuid);

                        String role;
                        if(uuid.equals(hunterID)) {
                            role = "Hunter";
                        } else {
                            role = "Runner";
                        }

                        ScoreboardManager manager = Bukkit.getScoreboardManager();
                        Scoreboard scoreboard = manager.getNewScoreboard();
                        Objective objective = scoreboard.registerNewObjective("main", "dummy", ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Tag");
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


                        Score hunterScore = objective.getScore(ChatColor.RED + "Hunter: " + ChatColor.WHITE + hunter.getName());
                        Score inGameScore = objective.getScore(ChatColor.RED + "Players: " + ChatColor.WHITE + alivePlayers.size() + "/" + (queuedPlayers.size() - 1));
                        Score roleScore = objective.getScore(ChatColor.RED + "Role: " + ChatColor.WHITE + role);
                        Score emptyScore = objective.getScore("");
                        Score timerScore = objective.getScore(ChatColor.RED + "Round Time: " + ChatColor.WHITE + shortInteger(roundTimer));

                        roleScore.setScore(5);
                        hunterScore.setScore(4);
                        inGameScore.setScore(3);
                        emptyScore.setScore(2);
                        timerScore.setScore(1);

                        p.setScoreboard(scoreboard);
                    });
                }
            }.runTaskTimer(plugin, 0, 20);
    }



    private void createScoreboard(UUID uuid, Player player) {
        String role;
        if(uuid.equals(hunterID)) {
            role = "Hunter";
        } else {
            role = "Runner";
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("main", "dummy", ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Tag");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        Score hunterScore = objective.getScore(ChatColor.RED + "Hunter: " + ChatColor.WHITE + hunter.getName());
        Score inGameScore = objective.getScore(ChatColor.RED + "Players: " + ChatColor.WHITE + alivePlayers.size() + "/" + (queuedPlayers.size() - 1));
        Score roleScore = objective.getScore(ChatColor.RED + "Role: " + ChatColor.WHITE + role);
        Score emptyScore = objective.getScore("");
        Score timerScore = objective.getScore(ChatColor.RED + "Round Time: " + ChatColor.WHITE + shortInteger(roundTimer));

        roleScore.setScore(5);
        hunterScore.setScore(4);
        inGameScore.setScore(3);
        emptyScore.setScore(2);
        timerScore.setScore(1);

        player.setScoreboard(scoreboard);
    }



    public void beginingCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(queuedPlayers.size() >= 3) {

                    startTimer--;
                    if(startTimer == 0) {
                        beginGame();
                        this.cancel();
                    }

                    if(startTimer < 6) {
                        queuedPlayers.forEach((UUID pUUID) -> {
                            Player player = Bukkit.getPlayer(pUUID);
                            player.sendMessage(prefix + "Game starts in: " + startTimer);
                        });
                    }
                } else {

                    queuedPlayers.forEach((UUID pUUID) -> {
                        Player player = Bukkit.getPlayer(pUUID);
                        player.sendMessage(prefix + "Not enough players to start the game!");
                    });
                    this.cancel();
                    startTimer = 30;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void listQueue(Player p) {
        p.sendMessage(prefix + "There are currently: " + queuedPlayers.size() + " players in queue!");
        p.sendMessage(prefix + queuedPlayers);
    }

    public void leaveQueue(Player p) {
        if(inProgress) {
            p.sendMessage(prefix + "You cannot leave during a game!");
        } else {
            queuedPlayers.remove(p.getUniqueId());
            alivePlayers.remove(p.getUniqueId());
            p.sendMessage(prefix + "You have left the queue");

            queuedPlayers.forEach((UUID uuid) -> {
                Player player = Bukkit.getPlayer(uuid);
                player.sendMessage(prefix + p.getName() + " has left the queue!");
            });
        }
    }


    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {

        if(e.getDamager().getType() == EntityType.PLAYER) {
            Player attacker = (Player) e.getDamager();

            if(e.getEntity().getType() == EntityType.PLAYER) {
                Player attacked = (Player) e.getEntity();

                e.setCancelled(true);
                if(attacker.getUniqueId() == hunterID) {
                    attacked.setGameMode(GameMode.SPECTATOR);
                    alivePlayers.remove(attacked.getUniqueId());

                    queuedPlayers.forEach((UUID uuid) -> {
                        Player player = Bukkit.getPlayer(uuid);
                        player.sendMessage(prefix + ChatColor.RED + attacked.getName() + ChatColor.WHITE + " has been " + ChatColor.BOLD + "" + ChatColor.RED + "eliminated" + ChatColor.stripColor("") + ChatColor.WHITE + "!");
                    });

                    if(alivePlayers.size() == 0) {
                        endGame();
                    }
                }
            }
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
                    endGame();
                }

                if(e.getPlayer().getUniqueId() == hunterID) {
                    endGame();
                }
            }
        }

    }

    public Game() {
        game = this;
    }

    public static Game getGame() {
        return game;
    }
}
