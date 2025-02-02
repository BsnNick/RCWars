package me.SgtMjrME.classUpdate.Abilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.object.Race;
import me.SgtMjrME.object.WarPlayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DrainLife extends BaseAbility {
	public final String disp;
	public final long delay;
	public final int cost;
	private final String desc;
	private HashSet<String> fired = new HashSet<String>();
	public final ItemStack item;

	public DrainLife(ConfigurationSection cs) {
		disp = ChatColor.translateAlternateColorCodes('&', cs.getString("display", "drainlife"));
		cost = cs.getInt("cost", 2);
		delay = cs.getLong("delay", 5000);
		desc = ChatColor.translateAlternateColorCodes('&', cs.getString("description", "(2 WP) Drain life from your enemy"));
		item = new ItemStack(Material.MAGMA_CREAM, 1);
		String s = "DrainLife";
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
		if (!(e.getEntity() instanceof Player))
			return false;
		if (!(e.getDamager() instanceof Player)) {
			if (!(e.getDamager() instanceof EnderPearl))
				return false;

			e.setDamage(e.getDamage() + 4);
			LivingEntity pl = (LivingEntity) ((EnderPearl) e.getDamager()).getShooter();
			pl.setHealth(pl.getHealth() + 2 > 20 ? 20 : pl.getHealth() + 2);
			return false;
		}
		Race r = WarPlayers.getRace(p);
		Race w = WarPlayers.getRace((Player) e.getEntity());
		if ((r == null) || (w == null))
			return false;
		if (r.equals(w))
			return false;
		e.setDamage(e.getDamage() + 6);
		p.setHealth(p.getHealth() + 2 > 20 ? 20 : p.getHealth() + 2);
		return false;
	}

	public boolean onInteract(Player p, PlayerInteractEvent e) {
		EnderPearl s = (EnderPearl) p.launchProjectile(EnderPearl.class);
		s.setShooter(p);
		e.setCancelled(true);

		fired.add(p.getName());
		return true;
	}

	public boolean OverrideAtt(Player p) {
		return fired.contains(p.getName());
	}

	public boolean OverrideTpt(Player p) {
		return fired.contains(p.getName());
	}

	public boolean onTeleport(PlayerTeleportEvent e) {
		if (!e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL))
			return false;
		e.setCancelled(true);
		final PlayerTeleportEvent out = e;
		Bukkit.getScheduler().runTaskLater(RCWars.returnPlugin(),
				new Runnable() {
					public void run() {
						fired.remove(out.getPlayer().getName());
					}
				}, 1L);
		return false;
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