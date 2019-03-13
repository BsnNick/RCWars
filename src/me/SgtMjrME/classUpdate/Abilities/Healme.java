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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.SgtMjrME.classUpdate.WarRank;

public class Healme extends BaseAbility {
	public final String disp;
	public final long delay;
	public final int cost;
	public final String desc;
	public final ItemStack item;

	public Healme(ConfigurationSection cs) {
		disp = ChatColor.translateAlternateColorCodes('&', cs.getString("display", "healme"));
		cost = cs.getInt("cost", 1);
		delay = cs.getLong("delay", 10000);
		desc = ChatColor.translateAlternateColorCodes('&', cs.getString("description", "(1 wp) Heals you"));
		item = new ItemStack(Material.SUGAR_CANE, 1);
		String s = "Healme";
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
		WarRank wr = WarRank.getPlayer(p);
		if (wr == null)
			return false;
		PotionEffect pot = new PotionEffect(PotionEffectType.REGENERATION, 10,
				wr.power, true);
		p.addPotionEffect(pot);
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