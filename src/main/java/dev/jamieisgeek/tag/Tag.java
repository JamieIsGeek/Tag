package dev.jamieisgeek.tag;

import dev.jamieisgeek.tag.Commands.TagCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Tag extends JavaPlugin {

    public Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {

        getCommand("pkt").setExecutor(new TagCommand());

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