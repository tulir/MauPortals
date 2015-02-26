package net.maunium.bukkit.MauPortals.API;

import java.sql.SQLException;

import org.bukkit.Location;

import net.maunium.bukkit.MauPortals.PortalBlockManager;
import net.maunium.bukkit.MauPortals.PortalData;

public class PortalManager {
	private static PortalBlockManager pbm;
	private static PortalData pd;
	
	public static void init(PortalBlockManager pbmm, PortalData pdd){
		pbm = pbmm;
		pd = pdd;
	}
	
	public static boolean removePortal(int id) {
		try {
			if (pbm.action(pd.getById(id), PortalBlockManager.DEACTIVATE, true)) {
				pd.removeEntry(id);
				return true;
			} else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int createPortal(Location c1, Location c2, String target) {
		try {
			int next = pd.max() + 1;
			if (next < 0) next = 0;
			pd.addEntry(next, c1, c2, target);
			if (pbm.action(pd.getById(next), PortalBlockManager.ACTIVATE, true)) return next;
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
