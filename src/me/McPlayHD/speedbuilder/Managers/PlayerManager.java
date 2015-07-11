package me.McPlayHD.speedbuilder.Managers;

import me.McPlayHD.speedbuilder.SpeedBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager {
	
	private SpeedBuilder plugin;

	public PlayerManager(SpeedBuilder plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void SpectatorJoin(final Player p) {
		p.getInventory().clear();
		plugin.spectating.add(p);
		p.teleport(plugin.spawnloc.clone().add(0,25,0));
		p.setScoreboard(SpeedBuilder.gm.sb);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setAllowFlight(true);
				p.setFlying(true);
				for(Player alle : Bukkit.getOnlinePlayers()) {
					if(p != alle) {
						p.hidePlayer(alle);
						alle.hidePlayer(p);
					}
				}
				p.showPlayer(plugin.spieler);
			}
		}.runTaskLater(plugin, 5);
		int rank = plugin.spectating.indexOf(p) + 1;
		p.sendMessage("§aDu bist auf Platz §e" + rank + " §ain der Warteschlaufe.");
	}

}
