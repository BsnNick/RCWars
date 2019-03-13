package me.SgtMjrME.object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;
import me.SgtMjrME.classUpdate.WarClass;
import me.SgtMjrME.classUpdate.WarRank;
import me.SgtMjrME.listeners.EntityListener;
import me.SgtMjrME.tasks.ScoreboardHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarPlayers {
	private static HashMap<UUID, dmgHold> lastDamage = new HashMap<UUID, dmgHold>();
	private static HashSet<UUID> allPlayers = new HashSet<UUID>();

	public static void clear() {
		for (Race r : Race.getAllRaces()) {
			r.clearPlayers();
		}
		lastDamage.clear();
	}

	public static void add(Player p, Race r) {
		allPlayers.add(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
		r.addPlayer(p, WarClass.defaultClass);
		RCWars.returnPlugin().announceState(p);
		if ((numPlayers() > 7)
				&& (RCWars.returnPlugin().isRunning()
						.equals(state.TOO_FEW_PLAYERS)))
			RCWars.returnPlugin().resumeGame();
	}

	public static void setRace(Player p, Race r) {
		add(p, r);
		ScoreboardHandler.updateTeam(p,r);
		p.teleport(r.getSpawn());
		WarClass.defaultClass.enterClass(p);
	}

	public static void removeAll(Location l) {
		for (Race r : Race.getAllRaces()) {
			for (UUID s : r.returnPlayers().keySet()) {
				Player p = RCWars.returnPlugin().getServer().getPlayer(s);
				if (p != null) {
					remove(p, l, "Removing all players");
				}
			}
			r.returnPlayers().clear();
		}
	}

	public static void remove(Player p, Location l, String reason) {
		if (p == null)
			return;
		remove(p, reason);
		p.teleport(l);
	}

	public static void remove(UUID p) {
		allPlayers.remove(p);
		getRace(p).removePlayer(p);
		lastDamage.remove(p);
		WarRank.pRank.remove(p);
		RCWars.repairing.remove(p);
		EntityListener.removeDmg(p);
	}

	public static void remove(Player p, String reason) {
		Race temp = getRace(p);
		if (temp == null) {
			return;
		}

//		WarPoints.saveWarPoints(p);

		if ((numPlayers() < 8)
				&& (RCWars.returnPlugin().isRunning().equals(state.RUNNING)))
			RCWars.returnPlugin().pauseGame();
		getRace(p).removePlayer(p);
		allPlayers.remove(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
		lastDamage.remove(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
		RCWars.repairing.remove(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
		EntityListener.removeDmg(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
		if ((p == null) || (!p.isValid())) {
			return;
		}

		WarRank wr = WarRank.getPlayer(p);
		if (wr != null)
			wr.leave(p);
		Util.sendMessage(p, "You have been removed from Wars: " + reason);
	}

	public void leave(Player p) {
		remove(p, RCWars.lobbyLocation(), "Player Quit");
	}

	public static Race getRace(Player p) {
		return getRace(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
	}

	public static Race getRace(UUID s) {
		for (Race race : Race.getAllRaces()) {
			if (race.hasPlayer(s))
				return race;
		}
		return null;
	}

	public static Iterator<UUID> listPlayers() {
		return allPlayers.iterator();
	}

	public static void setDamageTime(Player p, String prev) {
		setDamageTime(p.getUniqueId(), prev);
	}

	public static void setDamageTime(UUID name, String prev) {
		lastDamage.put(name, new dmgHold(System.currentTimeMillis(), prev));
	}

	public static boolean gotDamaged(Player p) {
		return gotDamaged(p.getUniqueId()); // Player#getName() -> Player#getUniqueId()
	}

	public static boolean gotDamaged(UUID name) {
		if (!lastDamage.containsKey(name)) {
			return false;
		}
		long old = ((dmgHold) lastDamage.get(name)).time.longValue();
		if (System.currentTimeMillis() - old > 5000L)
			return false;
		return true;
	}

	public static void removeDamaged(UUID name) {
		lastDamage.remove(name);
	}

	public static int numPlayers() {
		return allPlayers.size();
	}

	public static boolean isPlaying(UUID p) {
		return allPlayers.contains(p);
	}
}