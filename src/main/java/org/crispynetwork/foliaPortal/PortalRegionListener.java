package org.crispynetwork.foliaPortal.listeners;

import org.crispynetwork.foliaPortal.managers.PortalRegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PortalRegionListener implements Listener {

    private final PortalRegionManager manager;

    public PortalRegionListener(PortalRegionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        manager.checkPlayerLocation(event.getPlayer(), event.getFrom(), event.getTo());
    }
}