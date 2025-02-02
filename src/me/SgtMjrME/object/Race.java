package me.SgtMjrME.object;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;
import me.SgtMjrME.classUpdate.WarClass;
import me.SgtMjrME.tasks.ScoreboardHandler;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.Team;

public class Race {
	private final String name;
	private final String display;
	private final DyeColor color;
	private final ChatColor ccolor;
	private Location spawn;
	private Location spawnZll;
	private Location spawnZur;
	public final Material portalType;
	//public final byte portaldmg;
	public Axis portalDir;
	//public final MaterialData matdat;
	private final YamlConfiguration rcs;
	public final int swordtype;
	boolean ref = false;
	Team t;

	private static ConcurrentHashMap<Player, Race> setItemRace = new ConcurrentHashMap<Player, Race>();

	private static HashMap<String, Race> n2r = new HashMap<String, Race>();
	private ConcurrentHashMap<UUID, WarClass> p2c = new ConcurrentHashMap<UUID, WarClass>();

	public Race(YamlConfiguration cs) {
		rcs = cs;
		name = cs.getString("name").toLowerCase();
		color = DyeColor.valueOf(cs.getString("color"));
		ccolor = ChatColor.valueOf(cs.getString("ccolor"));
		display = cs.getString("name");
		//portalType = Material.getMaterial(cs.getString("portaltype")); // Only nether portals for now
		portalType = Material.NETHER_PORTAL;
		//portaldmg = ((byte) cs.getInt("portaldmg", 0));
		portalDir = Axis.valueOf(cs.getString("portalDirection", "X"));
		//matdat = portalType.getNewData(portaldmg);
		String temp = cs.getString("spawn");
		swordtype = cs.getInt("swordtype", 0);
		if (temp == null)
			spawn = null;
		else
			spawn = str2Loc(temp);
		if (cs.getBoolean("referee", false))
			openReferee(cs);
		else
			try {
				spawnZll = str2Loc(cs.getString("spawnzonel"));
				spawnZur = str2Loc(cs.getString("spawnzoner"));
			} catch (Exception e) {
				spawnZll = null;
				spawnZur = null;
			}
		n2r.put(name, this);
		ScoreboardHandler.registerTeam(name, ccolor);
	}

	private void openReferee(YamlConfiguration cs) {
		spawnZll = (this.spawnZur = null);
		ref = true;
	}

	static public void clear() {
		n2r.clear();
		setItemRace.clear();
		for (Race r : Race.getAllRaces())
			r.clearPlayers();
	}

	public void clearPlayers() {
		p2c.clear();
	}

	public static Race checkRaceOpen(Race r) {
		if (r.isRef())
			return r;
		HashMap<Race, Integer> numPerRace = new HashMap<Race, Integer>();
		Iterator<Race> tempRace = n2r.values().iterator();
		while (tempRace.hasNext()) {
			Race tr = (Race) tempRace.next();
			if (!tr.isRef())
				numPerRace.put(tr, 0);
		}
		Iterator<UUID> players = WarPlayers.listPlayers();
		Player p;
		while (players.hasNext()) {
			UUID pstring = players.next();
			p = RCWars.returnPlugin().getServer().getPlayer(pstring);
			if (p == null) {
				players.remove();
			} else {
				Race temp = WarPlayers.getRace(p);
				if (temp != null) {
					if (!temp.isRef())
						if (numPerRace.get(temp) == null)
							numPerRace.put(temp, Integer.valueOf(1));
						else
							numPerRace.put(temp, Integer
									.valueOf(((Integer) numPerRace.get(temp))
											.intValue() + 1));
				}
			}
		}
		if (numPerRace.isEmpty())
			return null;
		int trying = ((Integer) numPerRace.get(r)).intValue();
		Iterator<Race> i = numPerRace.keySet().iterator();
		while (i.hasNext()) {
			Race race = (Race) i.next();
			int thisrace = ((Integer) numPerRace.get(race)).intValue();
			if (thisrace + 1 < trying)
				return race;
		}
		if ((((Integer) numPerRace.get(r)).intValue() > 2)
				&& (RCWars.returnPlugin().isRunning().equals(state.STOPPED)))
			RCWars.returnPlugin().startGame();
		return r;
	}

	public void setSpawn(Location l) {
		spawn = l;
		rcs.set("spawn", RCWars.loc2str(l));
		try {
			rcs.save(RCWars.returnPlugin().getDataFolder() + "/Races/" + name
					+ ".yml");
		} catch (IOException e) {
			Util.sendLog("Crashed");
			e.printStackTrace();
		}
	}

	@Deprecated
	public Byte getColor() {
		return color.getWoolData();
	}
	
	public DyeColor getWoolColor() {
	    return color;
	}

	public String getName() {
		return name;
	}

	public Location getSpawn() {
		return spawn;
	}

	public static Race raceByName(String s) {
		return (Race) n2r.get(s.toLowerCase());
	}

	public static Collection<Race> getAllRaces() {
		return n2r.values();
	}

	private Location str2Loc(String s) {
		String[] s1 = s.split(" ");
		Location loc = new Location(Bukkit.getServer().getWorld(s1[0]),
				str2d(s1[1]), str2d(s1[2]), str2d(s1[3]));
		return loc;
	}

	private double str2d(String s) {
		return Double.parseDouble(s);
	}

	public void sendToSpawn(Player p) {
		if (spawn != null)
			p.teleport(spawn);
		else
			p.teleport(RCWars.lobbyLocation());
	}

	public String getDisplay() {
		return ccolor + display;
	}

	public ChatColor getCcolor() {
		return ccolor;
	}

	public boolean spawnZonesSet() {
		return (spawnZll != null) && (spawnZur != null);
	}

	private void save(String path, String item) {
		rcs.set(path, item);
		try {
			rcs.save(RCWars.returnPlugin().getDataFolder() + "/Races/" + name
					+ ".yml");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public int setSpawnProtect(Location protect, Player p) {
		if (spawnZll == null) {
			spawnZll = protect;
			return 1;
		}
		if (spawnZur == null) {
			spawnZur = protect;
			double xll = Math.min(spawnZll.getX(), spawnZur.getX());
			double xur = Math.max(spawnZll.getX(), spawnZur.getX());
			double yll = Math.min(spawnZll.getY(), spawnZur.getY());
			double yur = Math.max(spawnZll.getY(), spawnZur.getY());
			double zll = Math.min(spawnZll.getZ(), spawnZur.getZ());
			double zur = Math.max(spawnZll.getZ(), spawnZur.getZ());
			spawnZll = new Location(spawnZll.getWorld(), xll, yll, zll);
			spawnZur = new Location(spawnZll.getWorld(), xur, yur, zur);
			save("spawnzonel", RCWars.loc2str(spawnZll));
			save("spawnzoner", RCWars.loc2str(spawnZur));
			setItemRace.remove(p);
			return 2;
		}

		spawnZll = null;
		spawnZur = null;
		return 0;
	}

	public boolean inSpawn(Player p) {
		Location player = p.getLocation();
		return checkSpawnLocation(player);
	}

	private boolean checkSpawnLocation(Location p) {
		if (ref) return false;
		if ((spawnZll == null) || (spawnZur == null)){
			Util.sendLog("Check failed for spawn location on race " + getDisplay());
			return false;
		}
		if ((spawnZll.getX() > p.getX() || spawnZll.getZ() > p.getZ()
				|| spawnZll.getY() > p.getY()))
			return false;
		if ((spawnZur.getX() < p.getX() || spawnZur.getZ() < p.getZ()
				|| spawnZur.getY() < p.getY())) {
			return false;
		}
		return true;
	}

	public static boolean isOperating(Player player) {
		return setItemRace.containsKey(player);
	}

	public static void distributeAction(Player p, PlayerInteractEvent e) {
		int val = ((Race) setItemRace.get(p)).setSpawnProtect(e
				.getClickedBlock().getLocation(), p);
		if (val == 1)
			Util.sendMessage(e.getPlayer(), "First marker hit");
		else if (val == 2)
			Util.sendMessage(e.getPlayer(), "Second marker hit");
		else if (val == 3)
			Util.sendMessage(e.getPlayer(), "Zone removed");
	}

	public void setSpawnZone(Player p) {
		if (setItemRace.containsKey(p)) {
			setItemRace.remove(p);
			Util.sendMessage(p, "No longer setting zones");
			return;
		}
		if (setItemRace.containsValue(this)) {
			Util.sendMessage(p, ChatColor.DARK_RED
					+ "Someone is already editing this zone");
			return;
		}
		setItemRace.put(p, this);
		Util.sendMessage(p, ChatColor.DARK_GREEN + "Punch 2 corners of the zone");
	}

	public static int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
		}
		return 9001;
	}

	public void sendMessage(String mes) {
		Iterator<UUID> players = WarPlayers.listPlayers();
		while (players.hasNext()) {
			UUID s = players.next();
			Player send = Bukkit.getServer().getPlayer(s);
			if ((send != null) && (WarPlayers.getRace(s).equals(this)))
				Util.sendMessage(send, mes);
		}
	}

	public void addPlayer(Player p, WarClass class1) {
		UUID uuid = p.getUniqueId();
		WarClass wc = class1;
		p2c.put(uuid, wc); // Player#getName() -> Player#getUniqueId()
	}

	public void removePlayer(Player p) {
		removePlayer(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
	}

	public void removePlayer(UUID name) {
		p2c.remove(name);
	}

	public int getPlayersInRace() {
		return p2c.size();
	}

	public ConcurrentHashMap<UUID, WarClass> returnPlayers() {
		return p2c;
	}

	public boolean hasPlayer(Player p) {
		return hasPlayer(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
	}

	public boolean hasPlayer(UUID p) {
		return p2c.containsKey(p);
	}

	public boolean isRef() {
		return ref;
	}

	public static Race getRacePortal(Block b) { //TODO: handle portals
		for (Race r : n2r.values()) {
			/*if ((r.matdat.getItemTypeId() == b.getTypeId())
					&& (r.matdat.getData() == b.getData()))
		                return r;*/
		}
		
		return null;
	}
	
	public Team getTeam(){
		return t;
	}
	
	public Material getRaceWool()
	{
	    Material wool = Material.WHITE_WOOL;
	    if ((wool = Material.getMaterial(color.toString().toUpperCase() + "_WOOL")) == null)
	        return wool;
	    return Material.WHITE_WOOL;
	}
}