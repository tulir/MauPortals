package net.maunium.bukkit.MauPortals;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import net.maunium.bukkit.MauBukLib.MauUtils;
import net.maunium.bukkit.MauPortals.API.PortalHandler;

public class PlayerInteractListener implements Listener {
	private MauPortals plugin;
	
	public PlayerInteractListener(MauPortals plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent evt) {
		boolean left;
		if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) left = false;
		else if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) left = true;
		else return;
		
		ItemStack is = evt.getItem();
		if (is == null || is.getType() == null || is.getAmount() == 0) return;
		if (!is.hasItemMeta() || !is.getItemMeta().hasDisplayName()) return;
		
		if (is.getItemMeta().getDisplayName().equals("MauPortal Wand")) {
			evt.getPlayer().setMetadata(left ? plugin.sel1_meta : plugin.sel2_meta, new FixedMetadataValue(plugin, evt.getClickedBlock().getLocation()));
			evt.getPlayer().sendMessage(plugin.stag + "Set corner " + (left ? 1 : 2) + " to " + MauUtils.toReadableString(evt.getClickedBlock().getLocation()));
			evt.setCancelled(true);
		} else if (is.getItemMeta().getDisplayName().equals("MauPortal Inspector")) {
			Block b = evt.getClickedBlock();
			MetadataValue id = MauUtils.getMetadata(b, plugin.id_meta, plugin);
			MetadataValue data = MauUtils.getMetadata(b, plugin.target_meta, plugin);
			if (id != null && data != null && data.value() instanceof Location) {
				evt.getPlayer().sendMessage(plugin.stag + "Portal #" + id.asInt() + " @ " + b.getX() + ", " + b.getY() + ", " + b.getZ());
				if (data.value() instanceof Location) evt.getPlayer().sendMessage(ChatColor.GRAY + "Target: " + MauUtils.toReadableString((Location) data.value()));
				else if (data.value() instanceof PortalHandler) evt.getPlayer().sendMessage(ChatColor.GRAY + "Handler: " + ((PortalHandler) data.value()).getName());
			} else evt.getPlayer().sendMessage(plugin.errtag + "Not a MauPortal.");
		}
	}
}
