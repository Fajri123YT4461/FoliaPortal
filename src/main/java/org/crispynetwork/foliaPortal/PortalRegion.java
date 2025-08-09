package org.crispynetwork.foliaPortal.models;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortalRegion implements ConfigurationSerializable {

    private String name;
    private World world;
    private int minX, minY, minZ;
    private int maxX, maxY, maxZ;
    private String command;

    public PortalRegion(String name, Location pos1, Location pos2, String command) {
        this.name = name;
        Objects.requireNonNull(pos1.getWorld(), "Lokasi pertama tidak memiliki dunia!");
        Objects.requireNonNull(pos2.getWorld(), "Lokasi kedua tidak memiliki dunia!");
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            throw new IllegalArgumentException("Kedua posisi harus berada di dunia yang sama!");
        }

        this.world = pos1.getWorld();
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.command = command;
    }

    public PortalRegion(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.world = Bukkit.getWorld((String) map.get("world"));
        this.minX = (int) map.get("minX");
        this.minY = (int) map.get("minY");
        this.minZ = (int) map.get("minZ");
        this.maxX = (int) map.get("maxX");
        this.maxY = (int) map.get("maxY");
        this.maxZ = (int) map.get("maxZ");
        this.command = (String) map.get("command");

        if (this.world == null) {
            throw new IllegalArgumentException("Dunia tidak ditemukan untuk region: " + this.name);
        }
    }

    public boolean isInRegion(Location loc) {
        if (!loc.getWorld().equals(world)) {
            return false;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("world", world.getName());
        map.put("minX", minX);
        map.put("minY", minY);
        map.put("minZ", minZ);
        map.put("maxX", maxX);
        map.put("maxY", maxY);
        map.put("maxZ", maxZ);
        map.put("command", command);
        return map;
    }

    @Override
    public String toString() {
        return "PortalRegion{" +
                "name='" + name + '\'' +
                ", world=" + world.getName() +
                ", minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                ", command='" + command + '\'' +
                '}';
    }
}