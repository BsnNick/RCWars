package me.SgtMjrME.classUpdate.Abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.SgtMjrME.classUpdate.WarRank;

public class Feedme extends BaseAbility {
	public final String disp;
	public final long delay;
	public final int cost;
	private final String desc;
	public final ItemStack item;

	public Feedme(ConfigurationSection cs) {
		disp = ChatColor.translateAlternateColorCodes('&', cs.getString("display", "feedme"));
		cost = cs.getInt("cost", 3);
		delay = cs.getLong("delay", 60000);
		desc = ChatColor.translateAlternateColorCodes('&', cs.getString("description", "(3 WP) Feeds the player"));
		item = new ItemStack(Material.BOWL, 1);
		String s = "Feedme";
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(disp);
		if (s != null && s != ""){
			List<String> lore = new ArrayList<String>();
			lore.add(s);
			im.setLore(lore);
		}
		item.setItemMeta(im);
	}

	public boolean onInteract(Player p, PlayerInteractEvent e) {
		int pow = 1;
		WarRank wr = WarRank.getPlayer(p);
		if (wr != null)
			pow = wr.power;
		p.setFoodLevel(p.getFoodLevel() + pow);

		return true;
	}

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