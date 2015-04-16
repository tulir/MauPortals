package net.maunium.bukkit.MauPortals;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import net.maunium.bukkit.MauPortals.API.PortalHandler;
import net.maunium.bukkit.MauPortals.API.PortalManager;
import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;

public class MauPortals extends JavaPlugin {
	
	public String version;
	public final String name = "MauPortals", author = "Tulir293", stag = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + name + ChatColor.DARK_GREEN + "] "
			+ ChatColor.GRAY, errtag = ChatColor.DARK_RED + "[" + ChatColor.RED + name + ChatColor.DARK_RED + "] " + ChatColor.RED;
	public final String sel1_meta = "MauPortals_Selection1", sel2_meta = "MauPortals_Selection2", target_meta = "MauPortals_Target",
			id_meta = "MauPortals_PortalID", notpdim_meta = "MauPortals_DontTpToDim";
	private PortalData pd;
	private PortalBlockManager pbm;
	
	@Override
	public void onEnable() {
		long st = System.currentTimeMillis();
		version = getDescription().getVersion();
		saveDefaultConfig();
		
		String type = getConfig().getString("sql-type");
		switch (type.toLowerCase()) {
			case "sqlite":
				pd = PortalData.createSQLite(getDataFolder(), getConfig().getString("database"), getConfig().getString("table"));
				break;
			case "mysql":
				pd = PortalData.createMySQL(getConfig().getString("hostname"), getConfig().getInt("port"), getConfig().getString("database"), getConfig()
						.getString("username"), getConfig().getString("password"), getConfig().getString("table"));
				break;
		}
		pd.connect();
		pbm = new PortalBlockManager(this, pd);
		PortalManager.init(pbm, pd);
		
		try {
			pbm.action(pd.all(), PortalBlockManager.ACTIVATE, false);
		} catch (SQLException e) {
			getLogger().severe("Failed to load portals");
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(new PortalEnterListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getCommand("mauportals").setExecutor(new CommandPortals(this));
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info(name + " v" + version + " by " + author + " enabled in " + et + "ms.");
	}
	
	@Override
	public void onDisable() {
		long st = System.currentTimeMillis();
		
		pd.close();
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info(name + " v" + version + " by " + author + " disabled in " + et + "ms.");
	}
	
	public Object getPortalTarget(Location l) {
		MetadataValue mv = MetadataUtils.getMetadata(l.getBlock(), target_meta, this);
		if (mv != null && (mv.value() instanceof Location || mv.value() instanceof PortalHandler)) return mv.value();
		else return null;
	}
	
	public int getPortalId(Location l) {
		MetadataValue mv = MetadataUtils.getMetadata(l.getBlock(), id_meta, this);
		if (mv != null) return mv.asInt();
		else return -1;
	}
}