package dev.jamieisgeek.tag;

import dev.jamieisgeek.tag.Commands.TagCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Tag extends JavaPlugin {

    public Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {

        getCommand("tag").setExecutor(new TagCommand());
        getServer().getPluginManager().registerEvents(new Game(), this);

        new Game();
        logger.info("");
        logger.info("=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        logger.info("ParkourTag has Enabled!");
        logger.info("Version: 1.0");
        logger.info("=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        logger.info("");
    }

    @Override
    public void onDisable() {

    }
}
