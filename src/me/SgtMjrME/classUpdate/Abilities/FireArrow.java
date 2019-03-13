package me.SgtMjrME.classUpdate.Abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FireArrow extends BaseAbility {
	public final String disp;
	public final long delay;
	public final int cost;
	public final String desc;
	public final ItemStack item;

	public FireArrow(ConfigurationSection cs) {
		disp = ChatColor.translateAlternateColorCodes('&', cs.getString("display", "firearrow"));
		cost = cs.getInt("cost", 0);
		delay = cs.getLong("delay", 15000);
		desc = ChatColor.translateAlternateColorCodes('&', cs.getString("description", "Launches a fire arrow"));
		item = new ItemStack(Material.BLAZE_ROD, 1);
		String s = "FireArrow";
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(disp);
		if (s != null && s != ""){
			List<String> lore = new ArrayList<String>();
			lore.add(s);
			im.setLore(lore);
		}
		item.setItemMeta(im);
	}

	public boolean onAttack(Player p, EntityDamageByEntityEvent e) {
		if (((e.getDamager() instanceof Arrow))
				&& (e.getDamager().getFireTicks() > 0)) {
			e.getEntity().setFireTicks(100);
		}
		return false;
	}

	public boolean onLaunch(ProjectileLaunchEvent e) {
		if ((e.getEntity() instanceof Arrow)) {
			((Arrow) e.getEntity()).setFireTicks(600);
		}
		return true;
	}
	
	public boolean onInteract(Player p, PlayerInteractEvent e)
	{
		Arrow s = (Arrow)p.launchProjectile(Arrow.class);
		s.setShooter(p);
		e.setCancelled(true);
		
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