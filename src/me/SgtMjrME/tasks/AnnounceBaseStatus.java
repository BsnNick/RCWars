package me.SgtMjrME.tasks;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;
import me.SgtMjrME.object.Base;
import me.SgtMjrME.object.WarPlayers;

public class AnnounceBaseStatus implements Runnable {

	@Override
	public void run() {
		Iterator<Base> bases = Base.returnBases().iterator();
		while (bases.hasNext()) {
			Base b = bases.next();
			if (!b.willDisplay()) continue;
			Iterator<UUID> players = WarPlayers.listPlayers();
			while (players.hasNext()) {
				UUID pstring = players.next();
				Player p = RCWars.returnPlugin().getServer().getPlayer(pstring);
				if (p == null) {
					WarPlayers.remove(pstring);
					continue;
				}
				Util.sendMessage(p, b.getDisp());
			}
		}
	}

}