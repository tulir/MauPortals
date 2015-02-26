package net.maunium.bukkit.MauPortals.API;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.plugin.Plugin;

public class PortalHandlerRegistry {
	private static Map<String, PortalHandler> handlers = new HashMap<String, PortalHandler>();
	
	/**
	 * Registers a portal handler.
	 */
	public static void registerHandler(Plugin p, String name, PortalHandler handler) {
		handlers.put((p.getName() + ":" + name).toLowerCase(Locale.ENGLISH), handler);
	}
	
	/**
	 * Unregisters a portal handler.
	 * 
	 * @Deprecated It is not recommended to use this, as it may cause severe errors if a portal tries to use the removed handler.
	 */
	@Deprecated
	public static boolean unregisterHandler(Plugin p, String name) {
		String s = (p.getName() + ":" + name).toLowerCase(Locale.ENGLISH);
		if (handlers.containsKey(s)) {
			handlers.remove(s);
			return true;
		} else return false;
	}
	
	/**
	 * Get a handler. The key should be <code>pluginname:handlername</code>
	 */
	public static PortalHandler getHandler(String key) {
		return handlers.get(key.toLowerCase(Locale.ENGLISH));
	}
}
