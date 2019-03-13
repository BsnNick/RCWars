package me.SgtMjrME.classUpdate.Abilities;

import org.bukkit.inventory.ItemStack;

public class None extends BaseAbility {
	private final String disp = "none";
	private final long delay = 0L;
	private final int cost = 0;
	private final String desc = "No ability selected";
	private final ItemStack item = null;

	public String getDisplay() {
		return disp;
	}

	public long getDelay() {
		return delay;
	}

	public int getCost() {
		return cost;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}
}