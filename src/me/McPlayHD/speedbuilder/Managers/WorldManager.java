package me.McPlayHD.speedbuilder.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldManager {
	
	public static World loadWorld(String world, boolean Nether) {
		if(!isLoaded(world)) {
			WorldCreator wc = new WorldCreator(world);
			if(Nether) {
				wc.environment(Environment.NETHER);
			}
			return Bukkit.getServer().createWorld(wc);
		} else {
			return Bukkit.getWorld(world);
		}
	}
	
	public static boolean isLoaded(String world) {
		for (World w : Bukkit.getServer().getWorlds()) {
			if (w.getName().equals(world))  {
				return true;
			}
		}
		return false;
	}
	
	public static boolean unloadWorld(String world) {
		if(isLoaded(world)) {
			World w = Bukkit.getWorld(world);
			for (Player p : w.getPlayers())  {
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
			for(Chunk c : w.getLoadedChunks()) {
				c.unload();
			}
			boolean unload = Bukkit.unloadWorld(w, true);
			return unload;
		}
		return false;
	}

}
