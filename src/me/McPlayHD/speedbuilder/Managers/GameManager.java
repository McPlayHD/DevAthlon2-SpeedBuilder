package me.McPlayHD.speedbuilder.Managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;

import me.McPlayHD.speedbuilder.SpeedBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class GameManager {
	
	private SpeedBuilder plugin;

	public GameManager(SpeedBuilder plugin) {
		this.plugin = plugin;
	}
	
	public BukkitTask starttimer = null;
	public BukkitTask gametimer = null;
	public BukkitTask restartmsg = null;
	public BukkitTask resettimer = null;
	
	@SuppressWarnings("deprecation")
	public void Start(Player p) {
		if(plugin.spawnloc == null) {
			plugin.spawnloc = new Location(Bukkit.getWorld(plugin.getConfig().getString("Spawn.World")), plugin.getConfig().getDouble("Spawn.X"), plugin.getConfig().getDouble("Spawn.Y"), plugin.getConfig().getDouble("Spawn.Z"), (float) plugin.getConfig().getInt("Spawn.Yaw"), (float) plugin.getConfig().getInt("Spawn.Pitch"));
		}
		p.teleport(plugin.spawnloc);
		plugin.spieler = p;
		p.getInventory().clear();
		for(int i = 0; i < 9; i ++) {
			p.getInventory().addItem(new ItemStack(Material.SANDSTONE, 64));
		}
		for(Player alle : Bukkit.getOnlinePlayers()) {
			alle.showPlayer(p);
		}
		p.getInventory().setHeldItemSlot(0);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setHealth(20.0);
		p.setGameMode(GameMode.SURVIVAL);
		p.updateInventory();
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective ob = sb.registerNewObjective("SpeedBuilder", "dummy");
		ob.setDisplayName("§3SpeedBuilder");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob.getScore("§eTime§7:").setScore(1);
		ob.getScore("§eTime§7:").setScore(0);
		plugin.spieler.setScoreboard(sb);
		SpeedBuilder.sqlm.CreateConnection();
		Timer();
		topWallsUpdate();
	}

	public Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
	public Objective ob = sb.registerNewObjective("SpeedBuilder", "dummy");

	public void Timer() {
		starttimer = new BukkitRunnable() {
			@Override
			public void run() {
				if(plugin.starttime == 0) {
					plugin.spieler.sendMessage("§cDu blockierst das Spiel... Wir lassen auch andere Spielen.");
					Reset();
				}
			}
		}.runTaskLater(plugin, 600);
	}

	public void finalTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				SpeedBuilder.sqlm.addStats(plugin.spieler.getName(), "Games");
			}
		}.runTaskLater(plugin, 2);
		ob.setDisplayName("§3SpeedBuilder");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob.getScore("§eTime§7:").setScore(-1);
		ob.getScore("§eTime§7:").setScore(0);
		plugin.spieler.setScoreboard(sb);
		gametimer = new BukkitRunnable() {
			@Override
			public void run() {
				if(!plugin.bfinish) {
					int score = ob.getScore("§eTime§7:").getScore() + 1;
					ob.getScore("§eTime§7:").setScore(score);
					if(score >= 180) {
						Loose();
					}
				} else {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}

	@SuppressWarnings("deprecation")
	public void Loose() {
		plugin.bfinish = true;
		plugin.spieler.playSound(plugin.spieler.getLocation(), Sound.ANVIL_LAND, 10, 1);
		for(Player alle : Bukkit.getOnlinePlayers()) {
			alle.sendMessage("§cNächster Spieler in §e3 §cSekunden");
		}
		resettimer = new BukkitRunnable() {
			@Override
			public void run() {
				Reset();
			}
		}.runTaskLater(plugin, 60);
	}
	
	@SuppressWarnings("deprecation")
	public void Win() {
		long time = System.currentTimeMillis() - plugin.starttime;
		gametimer.cancel();
		plugin.bfinish = true;
		plugin.spieler.playSound(plugin.spieler.getLocation(), Sound.LEVEL_UP, 10, 1);
		SpeedBuilder.sqlm.addStats(plugin.spieler.getName(), "Wins");
		plugin.spieler.setAllowFlight(true);
		plugin.spieler.setFlying(true);
		String message = "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n"
				+ "§f                    §lSpeedBuilder\n"
				+ "§7 \n"
				+ "§7          §aBenötigte Zeit: §e" + (time/1000.0) + " §aSekunden\n"
				+ "§7 \n"
				+ "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
		for(Player alle : Bukkit.getOnlinePlayers()) {
			alle.sendMessage(message);
		}
		SpeedBuilder.sqlm.addTime(plugin.spieler, time);
		restartmsg = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player alle : Bukkit.getOnlinePlayers()) {
					alle.sendMessage("§cNächster Spieler in §e5 §cSekunden");
				}
			}
		}.runTaskLater(plugin, 20);
		resettimer = new BukkitRunnable() {
			@Override
			public void run() {
				Reset();
			}
		}.runTaskLater(plugin, 120);
	}

	public void Reset() {
		for(Block b : plugin.blocks) {
			b.setType(Material.AIR);
		}
		plugin.blocks.clear();
		plugin.bfinish = false;
		plugin.starttime = 0;
		if(starttimer != null && starttimer.isSync())
			starttimer.cancel();
		if(gametimer != null && gametimer.isSync())
			gametimer.cancel();
		if(restartmsg != null && restartmsg.isSync())
			restartmsg.cancel();
		if(resettimer != null && resettimer.isSync())
			resettimer.cancel();
		if(plugin.spectating.size() > 0) {
			Player spec = null;
			if(plugin.spieler != null) {
				spec = plugin.spieler;
				plugin.spieler = null;
			}
			Player next = plugin.spectating.get(0);
			plugin.spectating.remove(next);
			Start(next);
			if(spec != null) {
				SpeedBuilder.pm.SpectatorJoin(spec);
			}
			for(Player alle : plugin.spectating) {
				if(alle != spec) {
					int rank = plugin.spectating.indexOf(alle) + 1;
					alle.sendMessage("§aDu bist auf Platz §e" + rank + " §ain der Warteschlaufe.");
				}
			}
		} else {
			if(plugin.spieler != null) {
				Player next = plugin.spieler;
				plugin.spieler = null;
				Start(next);
			}
		}
	}

	public void topWallsUpdate() {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					Statement statement = plugin.c.createStatement();
					ResultSet res = statement.executeQuery("SELECT * FROM `BuilderPoints` ORDER BY Rank;");
					for(int i = 1; i<=5; i++) {
						if(res.next()) {
							while(res.getInt("Rank") == 0) {
								res.next();
							}
							World w = Bukkit.getWorld(plugin.getConfig().getString("Top." + i + ".Head.World"));
							double x = plugin.getConfig().getDouble("Top." + i + ".Head.X");
							double y = plugin.getConfig().getDouble("Top." + i + ".Head.Y");
							double z = plugin.getConfig().getDouble("Top." + i + ".Head.Z");
							Location loc = new Location(w, x, y, z);
							Skull s = (Skull) loc.getWorld().getBlockAt(loc).getState();
							String top = res.getString("Name");
							s.setOwner(top);
							s.update();
							World ws = Bukkit.getWorld(plugin.getConfig().getString("Top." + i + ".Sign.World"));
							double xs = plugin.getConfig().getDouble("Top." + i + ".Sign.X");
							double ys = plugin.getConfig().getDouble("Top." + i + ".Sign.Y");
							double zs = plugin.getConfig().getDouble("Top." + i + ".Sign.Z");
							Location locs = new Location(ws, xs, ys, zs);
							Sign ss = (Sign)locs.getWorld().getBlockAt(locs).getState();
							long points = res.getInt("Besttime");
							String staveragetime = "-";
							if(res.getInt("Wins") != 0) {
								long average = res.getLong("Totaltime")/res.getInt("Wins");
								staveragetime = (average/1000.0) + "";
							}
							ss.setLine(0, "#" + i);
							ss.setLine(1, top);
							ss.setLine(2, "Best: " + (points/1000.0));
							ss.setLine(3, "∅: " + staveragetime);
							ss.update();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
