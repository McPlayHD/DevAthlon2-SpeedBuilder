package me.McPlayHD.speedbuilder.Listeners;

import java.util.List;

import me.McPlayHD.speedbuilder.SpeedBuilder;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class PlayerListener implements Listener {

	private SpeedBuilder plugin;

	public PlayerListener(SpeedBuilder plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		if(name.length() > 14) {
			name = name.substring(0, 14);
		}
		p.setPlayerListName(ChatColor.GREEN + name);
		p.setDisplayName("§a" + p.getName());
		e.setJoinMessage(null);
		if(plugin.getConfig().getBoolean("Complete")) {
			if(plugin.spieler == null) {
				SpeedBuilder.gm.Start(p);
			} else {
				SpeedBuilder.pm.SpectatorJoin(p);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		Player p = e.getPlayer();
		if(plugin.spieler != null && plugin.spieler == p) {
			if(e.getBlockReplacedState().getType() != Material.AIR) {
				e.setCancelled(true);
				return;
			}
			if(e.getBlock().getType() == Material.SANDSTONE && !plugin.bfinish) {
				if(plugin.starttime == 0) {
					plugin.starttime = System.currentTimeMillis();
					SpeedBuilder.gm.finalTimer();
				}
				plugin.blocks.add(e.getBlock());
			} else {
				e.setCancelled(true);
			}
		} else {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		Player p = e.getPlayer();
		if(plugin.spieler != null && plugin.spieler == p) {
			if(p.getLocation().getWorld().getBlockAt(p.getLocation()).getTypeId() == 147) {
				if(!plugin.bfinish) {
					SpeedBuilder.gm.Win();
				}
			}
			if(p.getLocation().getY() < plugin.spawnloc.getY() - 10) {
				if(plugin.starttime != 0 && !plugin.bfinish) {
					p.setAllowFlight(true);
					p.setFlying(true);
					p.teleport(plugin.spawnloc.clone().add(0,1,0));
					SpeedBuilder.gm.Loose();
				} else {
					p.teleport(plugin.spawnloc);
				}
			}
		}
		if(plugin.spectating.contains(p)) {
			if(p.getLocation().getY() <= plugin.spawnloc.clone().add(0,15,0).getY()) {
				p.teleport(p.getLocation().add(0,3,0));
				p.setAllowFlight(true);
				p.setFlying(true);
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setQuitMessage(null);
		Player p = e.getPlayer();
		if(plugin.spieler != null && plugin.spieler == p) {
			plugin.spieler = null;
			if(!plugin.bfinish) {
				SpeedBuilder.gm.Reset();
			}
		}
		if(plugin.spectating.contains(p)) {
			plugin.spectating.remove(p);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		Player p = e.getPlayer();
		if(p.isOp()) {
			return;
		}
		String message = e.getMessage().toLowerCase();
		List<String> gameCmds = plugin.getConfig().getStringList("GameCommands");
		for(String cmd : gameCmds) {
			if(message.startsWith(cmd)) 
				return;
		}
		e.setCancelled(true);
		p.sendMessage("§cUnbekannter Kommand. §b/help §cfür Hilfe");
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage();
		e.setFormat(p.getDisplayName() + "§7: §f" + message);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGamemode(PlayerGameModeChangeEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		Player p = e.getPlayer();
		if(plugin.spieler == p) {
			if(e.getNewGameMode() == GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if(!plugin.getConfig().getBoolean("Complete"))
			return;
		e.setCancelled(true);
	}

}
