package me.SgtMjrME.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import me.SgtMjrME.object.Race;

public class SetHelmetColor implements Runnable {
	Race r;
	Player p;

	public SetHelmetColor(Race r, Player p) {
		this.r = r;
		this.p = p;
	}

	public void run() {
		ItemStack wool = new ItemStack(Material.WHITE_WOOL, 1);
		((Wool)wool.getData()).setColor(r.getWoolColor());
		p.getInventory().setHelmet(wool);
	}
}