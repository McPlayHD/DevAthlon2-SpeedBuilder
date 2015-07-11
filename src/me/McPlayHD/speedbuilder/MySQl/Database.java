package me.McPlayHD.speedbuilder.MySQl;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

public abstract class Database {

	protected Connection connection;
	
	protected Plugin plugin;

	protected Database(Plugin plugin) {
		this.plugin = plugin;
		this.connection = null;
	}

	public abstract Connection openConnection() throws SQLException,
			ClassNotFoundException;

	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean closeConnection() throws SQLException {
		if (connection == null) {
			return false;
		}
		connection.close();
		return true;
	}
}
