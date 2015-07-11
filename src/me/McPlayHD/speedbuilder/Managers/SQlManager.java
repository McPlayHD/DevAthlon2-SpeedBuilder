package me.McPlayHD.speedbuilder.Managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.McPlayHD.speedbuilder.SpeedBuilder;

public class SQlManager {
	
	private SpeedBuilder plugin;

	public SQlManager(SpeedBuilder plugin) {
		this.plugin = plugin;
	}
	
	public void CreateConnection() {
		try {
			if(plugin.c != null) {
				plugin.c.close();
			}
			plugin.c = plugin.MySQL.openConnection();
			Statement st = plugin.c.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `SpeedBuilder` ( `Name` TEXT NULL DEFAULT NULL , `UUID` TEXT NULL DEFAULT NULL , `Rank` INT NULL DEFAULT '0' , `Games` INT NOT NULL DEFAULT '0' , `Wins` INT NOT NULL DEFAULT '0' , `Besttime` BIGINT NOT NULL DEFAULT '0' , `Totaltime` BIGINT NOT NULL DEFAULT '0' ) ENGINE = InnoDB;");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addStats(final String name, final String stats) {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					CreateConnection();
					Statement statement = plugin.c.createStatement();
					ResultSet res = statement.executeQuery("SELECT * FROM `SpeedBuilder` WHERE Name = '" + name + "';");
					if(res.next()) {
						int newstats = (res.getInt(stats) + 1);
						Statement statement2 = plugin.c.createStatement();
						statement2.executeUpdate("UPDATE `SpeedBuilder` SET `" + stats + "`='" + newstats + "' WHERE Name = '" + name + "';");
					} else {
						Statement statement2 = plugin.c.createStatement();
						statement2.executeUpdate("INSERT INTO `SpeedBuilder` (`Name`, `UUID`, `" + stats + "`) VALUES ('" + name + "', '" + Bukkit.getPlayer(name).getUniqueId().toString() + "','1');");
					}
					if(!stats.equals("Wins")) {
						Ranktest();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addTime(final Player p, final long time) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Executors.newCachedThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						try {
							CreateConnection();
							String name = p.getName();
							Statement statement = plugin.c.createStatement();
							ResultSet res = statement.executeQuery("SELECT * FROM `SpeedBuilder` WHERE Name = '" + name + "';");
							if(res.next()) {
								long besttime = res.getLong("Besttime");
								if(besttime > time || besttime == 0) {
									Statement statement2 = plugin.c.createStatement();
									statement2.executeUpdate("UPDATE `SpeedBuilder` SET `Besttime`='" + time + "' WHERE Name = '" + name + "';");
									p.sendMessage("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n"
											+ "§f                    §lSpeedBuilder\n"
											+ "§7 \n"
											+ "§a          Neue persönliche Bestzeit!\n"
											+ "§7 \n"
											+ "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								}
								long total = res.getLong("Totaltime") + time;
								Statement statement2 = plugin.c.createStatement();
								statement2.executeUpdate("UPDATE `SpeedBuilder` SET `Totaltime`='" + total + "' WHERE Name = '" + name + "';");
							}
							Ranktest();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}.runTaskLater(plugin, 20);
	}

	public void Ranktest() {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					Statement statement = plugin.c.createStatement();
					ResultSet res = statement.executeQuery("SELECT * FROM `SpeedBuilder` ORDER BY Besttime;");
					int i = 0;
					ArrayList<String> ranglos = new ArrayList<String>();
					while(res.next()) {
						i ++;
						if(res.getInt("Rank") != i) {
							if(res.getLong("Besttime") > 0) {
								String name = res.getString("Name");
								Statement statement2 = plugin.c.createStatement();
								statement2.executeUpdate("UPDATE `SpeedBuilder` SET `Rank`='" + i + "' WHERE Name = '" + name + "';");
							} else {
								ranglos.add(res.getString("Name"));
								i --;
							}
						}
					}
					for(String st : ranglos) {
						Statement statement2 = plugin.c.createStatement();
						statement2.executeUpdate("UPDATE `SpeedBuilder` SET `Rank`='" + i + "' WHERE Name = '" + st + "';");
						i ++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
