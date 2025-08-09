package org.crispynetwork.foliaPortal;

import org.crispynetwork.foliaPortal.commands.PortalRegionCommand;
import org.crispynetwork.foliaPortal.listeners.PortalRegionListener;
import org.crispynetwork.foliaPortal.managers.PortalRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PortalRegionPlugin extends JavaPlugin {

    private static PortalRegionPlugin instance;
    private PortalRegionManager portalRegionManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("PortalRegionPlugin diaktifkan!");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();

        portalRegionManager = new PortalRegionManager(this);
        portalRegionManager.loadRegions();

        getCommand("portalregion").setExecutor(new PortalRegionCommand(portalRegionManager));
        getCommand("portalregion").setTabCompleter(new PortalRegionCommand(portalRegionManager));

        Bukkit.getPluginManager().registerEvents(new PortalRegionListener(portalRegionManager), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("PortalRegionPlugin dinonaktifkan!");
        portalRegionManager.saveRegions();
    }

    public static PortalRegionPlugin getInstance() {
        return instance;
    }

    public PortalRegionManager getPortalRegionManager() {
        return portalRegionManager;
    }
}