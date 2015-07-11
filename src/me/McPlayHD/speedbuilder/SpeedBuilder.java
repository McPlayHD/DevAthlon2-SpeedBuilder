package me.McPlayHD.speedbuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import me.McPlayHD.speedbuilder.Listeners.PlayerListener;
import me.McPlayHD.speedbuilder.Managers.GameManager;
import me.McPlayHD.speedbuilder.Managers.PlayerManager;
import me.McPlayHD.speedbuilder.Managers.SQlManager;
import me.McPlayHD.speedbuilder.Managers.WorldManager;
import me.McPlayHD.speedbuilder.MySQl.MySQL;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedBuilder extends JavaPlugin {

	public static SpeedBuilder instance;
	public static GameManager gm;
	public static PlayerManager pm;
	public static SQlManager sqlm;

	public static GameManager getGameManager() {return gm;}
	public static PlayerManager getPlayerManager() {return pm;}
	public static SQlManager getSQlManager() {return sqlm;}

	public String prefix = "§7[§eSpeedBuilder§7] ";

	public Player spieler = null;
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Player> spectating = new ArrayList<Player>();

	public MySQL MySQL = null;
	public Connection c = null;

	public long starttime = 0;
	public boolean bfinish = false;

	public Location spawnloc = null;

	public void onEnable() {
		instance = this;
		pm = new PlayerManager(this);
		gm = new GameManager(this);
		sqlm = new SQlManager(this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		if(getConfig().getBoolean("WorldSet")) {
			File src = new File("plugins/SpeedBuilder/saveworld");
			File dir = new File("SpeedBuilder");
			if(dir.exists()) {
				WorldManager.unloadWorld("SpeedBuilder");
				try {
					FileUtils.deleteDirectory(dir);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			try {
				FileUtils.copyDirectory(src, dir);
				WorldManager.loadWorld("SpeedBuilder", false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if(completeTest()) {
			getConfig().set("Complete", true);
		}
		if(getConfig().getBoolean("Complete")) {
			try {
				MySQL = new MySQL(
						this,
						getConfig().getString("MySQl.Ip"),
						getConfig().getString("MySQl.Port"),
						getConfig().getString("MySQl.Database"),
						getConfig().getString("MySQl.User"),
						getConfig().getString("MySQl.Password")
						);
				sqlm.CreateConnection();
			} catch(Exception ex) {
				System.out.println("Die MySQl-Daten sind nicht richtig in die config eingetragen.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("speedbuilder")) {
			if(sender instanceof Player) {
				final Player p = (Player)sender;
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("spawn")) {
						if(p.isOp()) {
							getConfig().set("Spawn.World", "SpeedBuilder");
							getConfig().set("Spawn.X", p.getLocation().getX());
							getConfig().set("Spawn.Y", p.getLocation().getY());
							getConfig().set("Spawn.Z", p.getLocation().getZ());
							getConfig().set("Spawn.Yaw", p.getLocation().getYaw());
							getConfig().set("Spawn.Pitch", p.getLocation().getPitch());
							saveConfig();
							reloadConfig();
							p.sendMessage("§aSpawnpoint gesetzt");
							if(completeTest()) {
								getConfig().set("Complete", true);
							}
						}
					}
					if(args[0].equalsIgnoreCase("world")) {
						if(p.isOp()) {
							getConfig().set("WorldSet", true);
							saveConfig();
							reloadConfig();
							File src = new File(p.getWorld().getName());
							File dir = new File("plugins/SpeedBuilder/saveworld");
							try {
								FileUtils.copyDirectory(src, dir);
								p.sendMessage(prefix + "§eDie Map wurde gespeichert");
								if(completeTest()) {
									getConfig().set("Complete", true);
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("top")) {
						if(p.isOp()) {
							if(args[1].equalsIgnoreCase("head")) {
								int top = Integer.parseInt(args[2]);
								if(top > 0 && top < 6) {
								if(p.getTargetBlock(null, 20).getState() instanceof Skull) {
									Skull b = (Skull)p.getTargetBlock(null, 20).getState();
									getConfig().set("Top." + top + ".Head.World", "SpeedBuilder");
									getConfig().set("Top." + top + ".Head.X", b.getLocation().getX());
									getConfig().set("Top." + top + ".Head.Y", b.getLocation().getY());
									getConfig().set("Top." + top + ".Head.Z", b.getLocation().getZ());
									saveConfig();
									reloadConfig();
									p.sendMessage("§aTop-Head gesetzt");
									if(completeTest()) {
										getConfig().set("Complete", true);
									}
								}
								} else {
									p.sendMessage("§cEs können nur 5 Top-Heads gesetzt werden (1-5)");
								}
							}
							if(args[1].equalsIgnoreCase("sign")) {
								int top = Integer.parseInt(args[2]);
								if(top > 0 && top < 6) {
									if(p.getTargetBlock(null, 20).getState() instanceof Sign) {
										Block b = p.getTargetBlock(null, 20);
										getConfig().set("Top." + top + ".Sign.World", "SpeedBuilder");
										getConfig().set("Top." + top + ".Sign.X", b.getLocation().getX());
										getConfig().set("Top." + top + ".Sign.Y", b.getLocation().getY());
										getConfig().set("Top." + top + ".Sign.Z", b.getLocation().getZ());
										saveConfig();
										reloadConfig();
										p.sendMessage("§aTop-Schild gesetzt");
										if(completeTest()) {
											getConfig().set("Complete", true);
										}
									}
								} else {
									p.sendMessage("§cEs können nur 5 Top-Schilder gesetzt werden (1-5)");
								}
							}
						}
					}
				}
			} else {
				sender.sendMessage("Du musst ein Spieler sein.");
			}
		}
		if(cmd.getName().equalsIgnoreCase("stats")) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				if(spieler == p || spectating.contains(p)) {
					if(args.length == 0) {
						Stats(p.getName(), p);
						return true;
					}
					if(args.length == 1) {
						Stats(args[0], p);
						return true;
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("help")) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				p.sendMessage("§7-= §aHelpPage §7=-\n"
						+ "§8- §e/stats (name)    §8Zeigt dir die Statistiken eines Spielers\n"
						+ "§7-==-");
				if(p.isOp()) {
					p.sendMessage("§7-= §aAdmin-HelpPage §7=-\n"
							+ "§8- §e/speedbuilder spawn    §8Setzt den Spawn\n"
							+ "§8- §e/speedbuilder world    §8Speichert die Welt\n"
							+ "§8- §e/speedbuilder top <head/sing> <1-5>    §8Top-5 Wand\n"
							+ "§7-==-");
				}
			}
		}
		return false;
	}

	public void Stats(final String p, final Player top) {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					String strank = "0";
					String stwins = "0";
					String stgames = "0";
					String stbesttime = "-";
					String staveragetime = "-";
					Statement statement = c.createStatement();
					ResultSet res = statement.executeQuery("SELECT * FROM `SpeedBuilder` WHERE Name = '" + p + "';");
					if(res.next()) {
						if(res.getInt("Rank") != 0) {
							strank = res.getString("Rank");
						} else {
							Statement st = c.createStatement();
							ResultSet re = st.executeQuery("SELECT * FROM `SpeedBuilder` WHERE 1 ORDER BY Rank DESC;");
							re.next();
							int rang = re.getInt("Rank") + 1;
							strank = rang + "";
						}
						stwins = res.getString("Wins");
						stgames = res.getString("Games");
						stbesttime = "" + (res.getLong("Besttime")/1000.0);
						long total = res.getLong("Totaltime");
						int wins = res.getInt("Wins");
						if(wins > 0) {
							long average = total/wins;
							staveragetime = (average/1000.0) + "";
						}
					} else {
						Statement st = c.createStatement();
						ResultSet re = st.executeQuery("SELECT * FROM `SpeedBuilder` WHERE 1 ORDER BY Rank DESC;");
						re.next();
						int rang = re.getInt("Rank") + 1;
						strank = rang + "";
					}
					top.sendMessage("§7-= §eStats von §6" + p + "§7 =-\n"
							+ "§3Position im Ranking: §e" + strank + "\n"
							+ "§3Beste Zeit: §e" +stbesttime + "\n"
							+ "§3Durchschnittliche Zeit: §e" +staveragetime + "\n"
							+ "§3Gespielte Spiele: §e" + stgames + "\n"
							+ "§3Gewonnene Spiele: §e" + stwins + "\n"
							+ "§7-==-");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public boolean completeTest() {
		if(getConfig().getBoolean("WorldSet")) {
			if(getConfig().get("Spawn.World") != null) {
				if(getConfig().get("Spawn.World") != null) {
					for(int i = 1; i <= 5; i ++) {
						if(getConfig().get("Top." + i + ".Sign.World") == null) {
							return false;
						}
						if(getConfig().get("Top." + i + ".Head.World") == null) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
