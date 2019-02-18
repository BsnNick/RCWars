package me.SgtMjrME.object;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;
import me.SgtMjrME.mysqlLink;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WarPoints {
	private static ConcurrentHashMap<UUID, Integer> warPointSave = new ConcurrentHashMap<UUID, Integer>();
	private static int warPointMax;
	private static mysqlLink mysql;
	private static RCWars rc;
	
	public WarPoints(int wpm, mysqlLink m, RCWars r){
		warPointMax = wpm;
		mysql = m;
		rc = r;
	}
	
	public static Boolean spendWarPoints(Player p, int cost) {
		if (warPointSave.containsKey(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			int points = warPointSave.get(p.getUniqueId()); // Player#getName() -> Player#getUniqueId() 
			if (points < cost) {
				Util.sendMessage(p, ChatColor.RED + "Not enough War Points");
				return false;
			}
			Util.sendMessage(p, ChatColor.GREEN + "You have been charged " + cost
					+ " warpoints");
			warPointSave.put(p.getUniqueId(), points - cost); // Player#getName() -> Player#getUniqueId()
			saveWPnoRemove(p);
			return true;
		}
		Util.sendMessage(p, ChatColor.RED + "War data not loaded");
		return false;
	}

	public static void giveWarPoints(Player player, int warPoints) {
		if (player.hasPermission("rcwars.rank6")) warPoints *= rc.rank6;
		else if (player.hasPermission("rcwars.rank5")) warPoints *= rc.rank5;
		else if (player.hasPermission("rcwars.rank4")) warPoints *= rc.rank4;
		else if (player.hasPermission("rcwars.rank3")) warPoints *= rc.rank3;
		else if (player.hasPermission("rcwars.rank2")) warPoints *= rc.rank2;
		else if (player.hasPermission("rcwars.rank1")) warPoints *= rc.rank1;
		if ((warPointSave.containsKey(player.getUniqueId())) // Player#getName() -> Player#getUniqueId()
				&& (((Integer) warPointSave.get(player.getUniqueId())) + warPoints > warPointMax)) { // Player#getName() -> Player#getUniqueId()
			Util.sendMessage(player, "You have hit the max of " + warPointMax);
			warPointSave.put(player.getUniqueId(), warPointMax); // Player#getName() -> Player#getUniqueId()
			if (mysql != null)
				mysql.updatePlayer(player, "wp", warPoints);
			return;
		}
		else if (!warPointSave.containsKey(player.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			warPointSave.put(player.getUniqueId(), warPoints); // Player#getName() -> Player#getUniqueId()
			if (mysql != null) mysql.updatePlayer(player, "wp", warPoints);
		} else { //how would this...
			warPointSave.put(player.getUniqueId(), warPointSave.get(player.getUniqueId()) + warPoints); // Player#getName() -> Player#getUniqueId()
			if (mysql != null) mysql.updatePlayer(player, "wp", warPoints);
		}
	}
	
	public static Integer getWarPoints(UUID s){
		if (!isLoaded(s)){
			loadWarPoints(s);
			return -1;//Hasn't loaded yet, they'll deal with it.
		}
		return (Integer) warPointSave.get(s);
	}

	public static Integer getWarPoints(Player p) {
		return (Integer) warPointSave.get(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
	}

	public static void loadWarPoints(final UUID p) {
		Bukkit.getScheduler().runTaskAsynchronously(rc, new Runnable(){
			@Override
			public void run() {
				int points = 0;
				try {
					BufferedReader b = new BufferedReader(new FileReader(new File(
							rc.getDataFolder() + "/WarPoints/"
									+ p + ".txt")));
					String temp = b.readLine();
					points = Integer.parseInt(temp);
					b.close();
				} catch (FileNotFoundException e) {
					Util.sendLog("File not found for player " + p);
				} catch (IOException e) {
					Util.sendLog("Error reading player " + p);
				} catch (Exception e) {
					Util.sendLog("Other Error with " + p);
				}
				warPointSave.put(p, points);
			}
		});
	}

	public static void saveWPnoRemove(final Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(rc, new Runnable(){
			@Override
			public void run() {
				if (warPointSave.containsKey(p.getUniqueId())) {
					int points = warPointSave.get(p.getUniqueId());
					try {
						File f = new File(rc.getDataFolder() + "/WarPoints");
						if (!f.exists())
							f.mkdir();
						BufferedWriter b = new BufferedWriter(
								new FileWriter(new File(rc.getDataFolder() + "/WarPoints/"
										+ p.getUniqueId() + ".txt"))); // Player#getName() -> Player#getUniqueId()
						b.write("" + points);
						b.close();
					} catch (IOException e) {
						Util.sendLog("Could not save player");
					}
				}
			}
		});
		
	}

	public static void saveWarPoints(final Player p) {
		if (warPointSave.containsKey(p.getUniqueId())) {
			saveWPnoRemove(p);
			Bukkit.getScheduler().runTaskLater(rc, new Runnable(){
				@Override
				public void run(){
					warPointSave.remove(p.getUniqueId());
				}
			}, 400);//Give it 20 seconds, should be done saving by then.
		}
	}

	public static void dispWP(Player p) {
		if (warPointSave.containsKey(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			Util.sendMessage(p, "You have " + warPointSave.get(p.getUniqueId()) // Player#getName() -> Player#getUniqueId()
					+ " warpoints");
		} else {
			Util.sendMessage(p, "Your war data is not loaded, attempting load");
			if (!isLoaded(p.getUniqueId())) Util.sendMessage(p, "Could not load)"); // Player#getName() -> Player#getUniqueId()
			else dispWP(p);
		}
	}
	
	public static boolean isLoaded(UUID s){ //Will return true if it loads, false otherwise (this will load the wp's)
		if (warPointSave.containsKey(s)) return true;
		loadWarPoints(s);
		return warPointSave.containsKey(s);//False if not contained, we have an issue.
	}
	
	public static boolean has(UUID name, double amt){
		if (isLoaded(name)){
			if (warPointSave.get(name) > amt) return true;
			return false;
		}
		return false;
	}
}
