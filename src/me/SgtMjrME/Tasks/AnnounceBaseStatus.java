package me.SgtMjrME.Tasks;

import java.util.Iterator;

import org.bukkit.entity.Player;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Object.WarPlayers;
import me.SgtMjrME.Object.Base;

public class AnnounceBaseStatus implements Runnable {

	@Override
	public void run() {
		Iterator<Base> bases = Base.returnBases().iterator();
		while (bases.hasNext()) {
			Base b = bases.next();
			Iterator<String> players = WarPlayers.listPlayers();
			while (players.hasNext()) {
				String pstring = players.next();
				Player p = RCWars.returnPlugin().getServer().getPlayer(pstring);
				if (p == null) {
					WarPlayers.remove(pstring);
					continue;
				}
				p.sendMessage(b.getDisp());
			}
		}
	}

}