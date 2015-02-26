package net.maunium.bukkit.MauPortals;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class PortalData {
	private Database sql;
	private String tableName;
	
	private PortalData(Database sql, String tableName) {
		this.tableName = tableName;
		this.sql = sql;
	}
	
	public void close() {
		sql.close();
	}
	
	public void connect() {
		if (!sql.isOpen()) sql.open();
		try {
			sql.query("CREATE TABLE " + tableName + "(ID INTEGER PRIMARY KEY,Corner1 TEXT NOT NULL,Corner2 TEXT NOT NULL,Target TEXT NOT NULL);");
		} catch (SQLException e) {}
	}
	
	public static PortalData createSQLite(File dataFolder, String database, String table) {
		return new PortalData(new SQLite(Logger.getLogger("Minecraft"), "[MauPortals] ", dataFolder.getAbsolutePath(), database, ".maudb"), table);
	}
	
	public static PortalData createMySQL(String hostname, int port, String database, String username, String password, String table) {
		return new PortalData(new MySQL(Logger.getLogger("Minecraft"), "[MauPortals] ", hostname, port, database, username, password), table);
	}
	
	public ResultSet addEntry(int id, Location c1, Location c2, String target) throws SQLException {
		return sql.query("INSERT OR REPLACE INTO " + tableName + " VALUES('" + id + "','" + format(c1) + "','" + format(c2) + "','" + target + "');");
	}
	
	public ResultSet removeEntry(int id) throws SQLException {
		return sql.query("DELETE FROM " + tableName + " WHERE ID=" + id + ";");
	}
	
	public ResultSet getById(int id) throws SQLException {
		return sql.query("SELECT * FROM " + tableName + " WHERE ID=" + id + ";");
	}
	
	public int max() throws SQLException {
		ResultSet rs = sql.query("SELECT MAX(ID) AS CurrentID FROM " + tableName + ";");
		return rs.getInt(1);
	}
	
	public ResultSet all() throws SQLException {
		return sql.query("SELECT * FROM " + tableName + ";");
	}
	
	public static String format(Location l) {
		return l.getWorld().getUID() + "|" + l.getBlockX() + "~" + l.getBlockY() + "~" + l.getBlockZ();
	}
	
	public static Location unformat(String s) {
		String[] s1 = s.split(Pattern.quote("|"));
		World w;
		try {
			w = Bukkit.getServer().getWorld(UUID.fromString(s1[0]));
		} catch (Exception e) {
			return null;
		}
		if (w == null) return null;
		String[] s2 = s1[1].split(Pattern.quote("~"));
		int x = 0, y = 0, z = 0;
		try {
			x = Integer.parseInt(s2[0]);
			y = Integer.parseInt(s2[1]);
			z = Integer.parseInt(s2[2]);
		} catch (NumberFormatException e) {
			return null;
		}
		return new Location(w, x, y, z);
	}
}
