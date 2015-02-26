package net.maunium.bukkit.MauPortals.API;

import org.bukkit.entity.Player;

public interface PortalHandler {
	/**
	 * Called when a player enters a portal with a custom handler.
	 * 
	 * @param p The player who entered the portal.
	 * @param portalId The ID of the portal, or -1 if couldn't be identified.
	 */
	public void enterPortal(Player p, int portalId);
	
	/**
	 * @return The name of the portal handler.
	 */
	public String getName();
}
