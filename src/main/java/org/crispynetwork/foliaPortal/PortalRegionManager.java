package org.crispynetwork.foliaPortal.managers;

import org.crispynetwork.foliaPortal.PortalRegionPlugin;
import org.crispynetwork.foliaPortal.models.PortalRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalRegionManager {

    private final PortalRegionPlugin plugin;
    private final Map<String, PortalRegion> regions;
    private final Map<UUID, String> playersInRegion;

    public PortalRegionManager(PortalRegionPlugin plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        this.playersInRegion = new HashMap<>();
        ConfigurationSerialization.registerClass(PortalRegion.class, "PortalRegion");
    }

    public void loadRegions() {
        regions.clear();
        FileConfiguration config = plugin.getConfig();
        if (config.isConfigurationSection("regions")) {
            for (String key : config.getConfigurationSection("regions").getKeys(false)) {
                try {
                    PortalRegion region = (PortalRegion) config.get("regions." + key);
                    if (region != null) {
                        regions.put(region.getName().toLowerCase(), region);
                        plugin.getLogger().info("Memuat region: " + region.getName());
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Gagal memuat region '" + key + "': " + e.getMessage());
                }
            }
        }
        plugin.getLogger().info(regions.size() + " portal region telah dimuat.");
    }

    public void saveRegions() {
        FileConfiguration config = plugin.getConfig();
        config.set("regions", null);
        for (PortalRegion region : regions.values()) {
            config.set("regions." + region.getName(), region);
        }
        try {
            config.save(plugin.getConfig().getCurrentPath());
            plugin.getLogger().info("Portal region telah disimpan.");
        } catch (Exception e) {
            plugin.getLogger().severe("Gagal menyimpan portal region: " + e.getMessage());
        }
    }

    public void addRegion(PortalRegion region) {
        regions.put(region.getName().toLowerCase(), region);
        saveRegions();
        plugin.getLogger().info("Region '" + region.getName() + "' ditambahkan.");
    }

    public void removeRegion(String name) {
        if (regions.remove(name.toLowerCase()) != null) {
            saveRegions();
            plugin.getLogger().info("Region '" + name + "' dihapus.");
        }
    }

    public PortalRegion getRegion(String name) {
        return regions.get(name.toLowerCase());
    }

    public Collection<PortalRegion> getAllRegions() {
        return regions.values();
    }

    public void checkPlayerLocation(Player player, Location from, Location to) {
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        String currentRegionName = playersInRegion.get(player.getUniqueId());
        PortalRegion enteredRegion = null;

        for (PortalRegion region : regions.values()) {
            if (region.isInRegion(to)) {
                enteredRegion = region;
                break;
            }
        }

        if (enteredRegion != null) {
            if (currentRegionName == null || !currentRegionName.equalsIgnoreCase(enteredRegion.getName())) {
                playersInRegion.put(player.getUniqueId(), enteredRegion.getName());
                final PortalRegion finalEnteredRegion = enteredRegion;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    String finalCommand = finalEnteredRegion.getCommand().replace("%player%", player.getName());
                    plugin.getLogger().info("Mengeksekusi perintah untuk " + player.getName() + " di region " + finalEnteredRegion.getName() + ": " + finalCommand);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                });
            }
        } else {
            if (currentRegionName != null) {
                playersInRegion.remove(player.getUniqueId());
                plugin.getLogger().info(player.getName() + " keluar dari region " + currentRegionName);
            }
        }
    }
}