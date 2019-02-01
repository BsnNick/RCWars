package me.SgtMjrME.tasks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.SgtMjrME.object.Race;

public class SetHelmetColor implements Runnable {
	Race r;
	Player p;

	public SetHelmetColor(Race r, Player p) {
		this.r = r;
		this.p = p;
	}

	public void run() {
		byte color = 0;
		if (r != null)
			color = r.getColor().byteValue();
		ItemStack wool = new ItemStack(35, 1, color);
		p.getInventory().setHelmet(wool);
	}
}