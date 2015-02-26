package net.maunium.bukkit.MauPortals;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.metadata.FixedMetadataValue;

import net.maunium.bukkit.MauPortals.API.PortalHandler;

public class PortalEnterListener implements Listener {
	private MauPortals plugin;
	
	public PortalEnterListener(MauPortals plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPortalEnter(EntityPortalEnterEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player p = (Player) evt.getEntity();
			
			if (p.getGameMode().equals(GameMode.CREATIVE) && evt.getLocation().getBlock().getType().equals(Material.PORTAL) || evt.getLocation().getBlock().getType().equals(Material.ENDER_PORTAL))
				p.setMetadata(plugin.notpdim_meta, new FixedMetadataValue(plugin, true));
			
			Object o = plugin.getPortalTarget(evt.getLocation());
			
			if (o instanceof Location) {
				p.teleport((Location) o);
				p.setFallDistance(0.0F);
			} else if (o instanceof PortalHandler) ((PortalHandler) o).enterPortal(p, plugin.getPortalId(evt.getLocation()));
			else p.sendMessage(plugin.errtag + "Invalid portal target/handler: " + o);
		}
	}
	
	@EventHandler
	public void onPortalTeleport(PlayerPortalEvent evt) {
		if (evt.getPlayer().hasMetadata(plugin.notpdim_meta)) {
			evt.setCancelled(true);
			evt.getPlayer().removeMetadata(plugin.notpdim_meta, plugin);
		}
	}
}
