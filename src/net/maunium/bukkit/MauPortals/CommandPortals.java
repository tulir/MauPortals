package net.maunium.bukkit.MauPortals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import net.maunium.bukkit.MauBukLib.IngameCommandExecutor;
import net.maunium.bukkit.MauBukLib.MauUtils;
import net.maunium.bukkit.MauPortals.API.PortalHandlerRegistry;
import net.maunium.bukkit.MauPortals.API.PortalHandler;
import net.maunium.bukkit.MauPortals.API.PortalManager;

public class CommandPortals extends IngameCommandExecutor {
	private MauPortals plugin;
	
	public CommandPortals(MauPortals plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(Player p, Command cmd, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("wand")) {
				ItemStack is = new ItemStack(Material.SPECKLED_MELON);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("MauPortal Wand");
				is.setItemMeta(im);
				p.getInventory().addItem(is);
				return true;
			} else if (args[0].equalsIgnoreCase("inspector")) {
				ItemStack is = new ItemStack(Material.SHEARS);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("MauPortal Inspector");
				is.setItemMeta(im);
				p.getInventory().addItem(is);
				return true;
			} else if (args[0].equalsIgnoreCase("create")) {
				MetadataValue c1mv = MauUtils.getMetadata(p, plugin.sel1_meta, plugin);
				MetadataValue c2mv = MauUtils.getMetadata(p, plugin.sel2_meta, plugin);
				
				if (c1mv != null && c2mv != null && c1mv.value() instanceof Location && c2mv.value() instanceof Location) {
					Location c1 = (Location) c1mv.value();
					Location c2 = (Location) c2mv.value();
					String target = null;
					if (args.length == 2) {
						PortalHandler ph = PortalHandlerRegistry.getHandler(args[1]);
						if (ph == null) {
							p.sendMessage(plugin.errtag + "Portal handler " + args[1] + " could not be found.");
							return true;
						} else target = args[1];
					} else if (args.length > 3) {
						int x, y, z;
						
						try {
							x = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							p.sendMessage(plugin.errtag + "Could not parse X-coordinate " + args[1]);
							return true;
						}
						
						try {
							y = Integer.parseInt(args[2]);
						} catch (NumberFormatException e) {
							p.sendMessage(plugin.errtag + "Could not parse Y-coordinate " + args[2]);
							return true;
						}
						
						try {
							z = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							p.sendMessage(plugin.errtag + "Could not parse Z-coordinate " + args[3]);
							return true;
						}
						
						World w;
						if (args.length > 4) {
							w = plugin.getServer().getWorld(args[4]);
							if (w == null) {
								p.sendMessage(plugin.errtag + "World " + args[4] + " not found.");
								return true;
							}
						} else w = p.getLocation().getWorld();
						
						target = MauUtils.toString(new Location(w, x, y, z));
					} else target = MauUtils.toString(p.getLocation());
					int i = PortalManager.createPortal(c1, c2, target);
					if (i != -1) p.sendMessage(plugin.stag + "Created portal with ID " + i);
					else p.sendMessage(plugin.errtag + "Failed to create portal (See console)");
				} else p.sendMessage(plugin.errtag + "Select an area with the MauPortal Wand first.");
				return true;
			} else if (args[0].equalsIgnoreCase("remove") && args.length > 1) {
				int id;
				try {
					id = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					p.sendMessage(plugin.errtag + "The second argument must be an integer.");
					return true;
				}
				if (PortalManager.removePortal(id)) p.sendMessage(plugin.stag + "Removed portal #" + id + " successfully.");
				else p.sendMessage(plugin.errtag + "Failed to remove portal #" + id + " (See console)");
				return true;
			}
		}
		return false;
	}
}
