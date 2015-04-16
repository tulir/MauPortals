package net.maunium.bukkit.MauPortals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import net.maunium.bukkit.MauPortals.API.PortalHandlerRegistry;
import net.maunium.bukkit.Maussentials.Utils.SerializableLocation;

public class PortalBlockManager {
	public MauPortals plugin;
	public PortalData pd;
	
	public PortalBlockManager(MauPortals plugin, PortalData pd) {
		this.plugin = plugin;
		this.pd = pd;
	}
	
	public static final int ACTIVATE = 0, DEACTIVATE = 1;
	
	public boolean action(ResultSet rs, int actionid, boolean returnOnError) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt("ID");
			plugin.getLogger().info("Activating portal #" + id);
			String c1s = rs.getString("Corner1");
			String c2s = rs.getString("Corner2");
			String targets = rs.getString("Target");
			
			Location c1 = PortalData.unformat(c1s);
			Location c2 = PortalData.unformat(c2s);
			Object target = null;
			if (targets.contains(":")) {
				target = PortalHandlerRegistry.getHandler(targets);
				if (target == null) {
					plugin.getLogger().severe("Could not find handler \"" + targets + "\" for portal #" + id);
					pd.removeEntry(id);
					if (returnOnError) return false;
					else continue;
				}
			} else {
				target = SerializableLocation.fromString(targets).toLocation();
				if (target == null) {
					plugin.getLogger().severe("Could not parse target \"" + targets + "\" for portal #" + id);
					pd.removeEntry(id);
					if (returnOnError) return false;
					else continue;
				}
			}
			
			if (c1 == null || c2 == null) {
				plugin.getLogger().severe("Failed to parse portal #" + id + " (Null locations)");
				pd.removeEntry(id);
				if (returnOnError) return false;
				else continue;
			}
			
			if (!c1.getWorld().equals(c2.getWorld())) {
				plugin.getLogger().severe("Error: The corners of portal #" + id + " are in different worlds");
				pd.removeEntry(id);
				if (returnOnError) return false;
				else continue;
			}
			
			World w = c1.getWorld();
			
			int xdir = Integer.compare(c1.getBlockX(), c2.getBlockX());
			int ydir = Integer.compare(c1.getBlockY(), c2.getBlockY());
			int zdir = Integer.compare(c1.getBlockZ(), c2.getBlockZ());
			
			for (int x = c1.getBlockX(); compare(x, c2.getBlockX(), xdir); x += change(xdir)) {
				for (int y = c1.getBlockY(); compare(y, c2.getBlockY(), ydir); y += change(ydir)) {
					for (int z = c1.getBlockZ(); compare(z, c2.getBlockZ(), zdir); z += change(zdir)) {
						Block b = w.getBlockAt(x, y, z);
						if (actionid == ACTIVATE) {
							b.setMetadata(plugin.id_meta, new FixedMetadataValue(plugin, id));
							b.setMetadata(plugin.target_meta, new FixedMetadataValue(plugin, target));
						} else if (actionid == DEACTIVATE) {
							b.removeMetadata(plugin.id_meta, plugin);
							b.removeMetadata(plugin.target_meta, plugin);
						}
					}
				}
			}
		}
		return true;
	}
	
	public int change(int direction) {
		return direction == 1 ? -1 : 1;
	}
	
	public boolean compare(int a, int b, int direction) {
		if (direction == 1) return a >= b;
		else if (direction == -1) return a <= b;
		else return a == b;
	}
}
