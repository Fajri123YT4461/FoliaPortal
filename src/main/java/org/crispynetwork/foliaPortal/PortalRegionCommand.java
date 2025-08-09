package org.crispynetwork.foliaPortal.commands;

import org.crispynetwork.foliaPortal.managers.PortalRegionManager;
import org.crispynetwork.foliaPortal.models.PortalRegion;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PortalRegionCommand implements CommandExecutor, TabCompleter {

    private final PortalRegionManager manager;
    private final Map<UUID, Location> playerPos1 = new HashMap<>();
    private final Map<UUID, Location> playerPos2 = new HashMap<>();

    public PortalRegionCommand(PortalRegionManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cHanya pemain yang bisa menggunakan perintah ini.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("portalregion.admin")) {
            player.sendMessage("§cAnda tidak memiliki izin untuk menggunakan perintah ini.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "pos1":
                playerPos1.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("§aPosisi 1 diatur di lokasi Anda saat ini: §f" + formatLocation(player.getLocation()));
                break;
            case "pos2":
                playerPos2.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("§aPosisi 2 diatur di lokasi Anda saat ini: §f" + formatLocation(player.getLocation()));
                break;
            case "create":
                if (args.length < 3) {
                    player.sendMessage("§cUsa: /pr create <nama_region> <perintah>");
                    player.sendMessage("§aContoh: /pr create myportal say Hello %player%");
                    return true;
                }
                Location p1 = playerPos1.get(player.getUniqueId());
                Location p2 = playerPos2.get(player.getUniqueId());

                if (p1 == null || p2 == null) {
                    player.sendMessage("§cAnda harus mengatur Posisi 1 dan Posisi 2 terlebih dahulu menggunakan /pr pos1 dan /pr pos2.");
                    return true;
                }

                String regionName = args[1];
                if (manager.getRegion(regionName) != null) {
                    player.sendMessage("§cRegion dengan nama '" + regionName + "' sudah ada.");
                    return true;
                }

                StringBuilder cmdBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    cmdBuilder.append(args[i]).append(" ");
                }
                String commandToExecute = cmdBuilder.toString().trim();

                try {
                    PortalRegion newRegion = new PortalRegion(regionName, p1, p2, commandToExecute);
                    manager.addRegion(newRegion);
                    player.sendMessage("§aRegion portal '" + regionName + "' berhasil dibuat.");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cGagal membuat region: " + e.getMessage());
                }
                break;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage("§cUsa: /pr remove <nama_region>");
                    return true;
                }
                String nameToRemove = args[1];
                if (manager.getRegion(nameToRemove) == null) {
                    player.sendMessage("§cRegion dengan nama '" + nameToRemove + "' tidak ditemukan.");
                    return true;
                }
                manager.removeRegion(nameToRemove);
                player.sendMessage("§aRegion '" + nameToRemove + "' berhasil dihapus.");
                break;
            case "list":
                if (manager.getAllRegions().isEmpty()) {
                    player.sendMessage("§fBelum ada portal region yang dibuat.");
                    return true;
                }
                player.sendMessage("§b--- Daftar Portal Region ---");
                for (PortalRegion region : manager.getAllRegions()) {
                    player.sendMessage("§f- §a" + region.getName() + "§f: Dunia " + region.getWorld().getName() +
                            ", Perintah: §e" + region.getCommand());
                }
                player.sendMessage("§b--------------------------");
                break;
            case "reload":
                manager.loadRegions();
                player.sendMessage("§aKonfigurasi portal region berhasil dimuat ulang.");
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§b--- Bantuan PortalRegion ---");
        player.sendMessage("§f/pr pos1 §7- Mengatur posisi pertama untuk region.");
        player.sendMessage("§f/pr pos2 §7- Mengatur posisi kedua untuk region.");
        player.sendMessage("§f/pr create <nama> <perintah> §7- Membuat region baru dengan perintah. Gunakan %player% sebagai placeholder nama pemain.");
        player.sendMessage("§f/pr remove <nama> §7- Menghapus region yang sudah ada.");
        player.sendMessage("§f/pr list §7- Menampilkan daftar semua region yang dibuat.");
        player.sendMessage("§f/pr reload §7- Memuat ulang konfigurasi region dari file.");
        player.sendMessage("§b-----------------------------");
    }

    private String formatLocation(Location loc) {
        return String.format("Dunia: %s, X: %d, Y: %d, Z: %d",
                loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("pos1", "pos2", "create", "remove", "list", "reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return manager.getAllRegions().stream()
                        .map(PortalRegion::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}