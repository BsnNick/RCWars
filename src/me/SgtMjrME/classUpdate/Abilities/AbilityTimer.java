package me.SgtMjrME.classUpdate.Abilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;
import me.SgtMjrME.object.WarPoints;
import me.SgtMjrME.tasks.AbilityCooldown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AbilityTimer {
	private static HashMap<String, HashSet<Cooldown>> times = new HashMap<String, HashSet<Cooldown>>();

	public static final HashMap<String, BaseAbility> str2abil = new HashMap<String, BaseAbility>();

	public AbilityTimer(Configuration cs) {
		str2abil.put(ChatColor.stripColor(cs.getString("boost.display")),
				new Boost(cs.getConfigurationSection("boost")));
		str2abil.put(ChatColor.stripColor(cs.getString("cloak.display")),
				new Cloak(cs.getConfigurationSection("cloak")));
		str2abil.put(ChatColor.stripColor(cs.getString("drainlife.display")),
				new DrainLife(cs.getConfigurationSection("drainlife")));
		str2abil.put(ChatColor.stripColor(cs.getString("feedme.display")),
				new Feedme(cs.getConfigurationSection("feedme")));
		str2abil.put(ChatColor.stripColor(cs.getString("firearrow.display")),
				new FireArrow(cs.getConfigurationSection("firearrow")));
		str2abil.put(ChatColor.stripColor(cs.getString("fireball.display")),
				new Fireball(cs.getConfigurationSection("fireball")));
		str2abil.put(ChatColor.stripColor(cs.getString("firenova.display")),
				new Firenova(cs.getConfigurationSection("firenova")));
		str2abil.put(ChatColor.stripColor(cs.getString("firestorm.display")),
				new FireStorm(cs.getConfigurationSection("firestorm")));
		str2abil.put(ChatColor.stripColor(cs.getString("healgroup.display")),
				new HealGroup(cs.getConfigurationSection("healgroup")));
		str2abil.put(ChatColor.stripColor(cs.getString("healme.display")),
				new Healme(cs.getConfigurationSection("healme")));
		str2abil.put(ChatColor.stripColor(cs.getString("healplayer.display")),
				new HealPlayer(cs.getConfigurationSection("healplayer")));
//		str2abil.put("none", new None());
		str2abil.put(ChatColor.stripColor(cs.getString("rally.display")),
				new Rally(cs.getConfigurationSection("rally")));
		str2abil.put(ChatColor.stripColor(cs.getString("sap.display")),
				new Sap(cs.getConfigurationSection("sap")));
		str2abil.put(ChatColor.stripColor(cs.getString("strike.display")),
				new Strike(cs.getConfigurationSection("strike")));
		str2abil.put(ChatColor.stripColor(cs.getString("volley.display")),
				new Volley(cs.getConfigurationSection("volley")));
	}

	public static void addCooldown(Player p, BaseAbility b) {
		HashSet<Cooldown> cdtimer = times.get(p.getName());
		if (cdtimer == null) {
			times.put(p.getName(), new HashSet<Cooldown>());
			cdtimer = times.get(p.getName());
		}
		Iterator<Cooldown> cd = cdtimer.iterator();
		while (cd.hasNext())
			if (((Cooldown) cd.next()).a.equals(b))
				cd.remove();
		cdtimer.add(new Cooldown(b));
		times.put(p.getName(), cdtimer);
		final Player pl = p;
		final BaseAbility ba = b;
		Bukkit.getScheduler().runTaskLater(RCWars.returnPlugin(),
				new Runnable() {
					public void run() {
						Util.sendMessage(pl, ChatColor.GREEN + "Ability "
								+ ba.getDisplay() + " is ready to be used");
					}
				}, b.getDelay() / 1000L * 20L);
	}

	public static void removeAllCooldown(Player p) {
		times.remove(p.getName());
	}

	public static boolean checkTime(Player p, BaseAbility b) {
		HashSet<Cooldown> cdtimer = times.get(p.getName());
		if (cdtimer == null) {
			addCooldown(p, b);
			return true;
		}

		Iterator<Cooldown> i = cdtimer.iterator();
		while (i.hasNext()) {
			Cooldown c = (Cooldown) i.next();

			if (c.a.equals(b)) {
				boolean go = System.currentTimeMillis() - c.time.longValue() > b
						.getDelay();
				if (!go)
					Util.sendMessage(p, ChatColor.GRAY
							+ "Ability is not ready yet ("
							+ (b.getDelay() - (System.currentTimeMillis() - c.time))
							/ 1000L + "s)");
				return System.currentTimeMillis() - c.time.longValue() > b
						.getDelay();
			}
		}

		return true;
	}

	public static void onAttack(Player p, EntityDamageByEntityEvent e) {
		BaseAbility b = getAbility(p);

		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if ((!b.OverrideAtt(p)) && (!checkTime(p, b)))
			return;

		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}

		if (b.onAttack(p, e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
//			Util.sendMessage(p, ChatColor.GREEN + "You have used ability "
//					+ b.getDisplay());
//			addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}

	public static void onDefend(Player p, EntityDamageByEntityEvent e) {
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if ((!b.OverrideDef(p)) && (!checkTime(p, b)))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onDefend(p, e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
//			Util.sendMessage(p, ChatColor.GREEN + "You have used ability "
//					+ b.getDisplay());
//			addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}

	public static void onInteract(Player p, PlayerInteractEvent e) {
		BaseAbility b = getAbility(p);
		if (b == null){
			if ((b = getUsedAbility(p)) != null && b.allowUsed()){
				b.onInteractUsed(p, e);
			}
			return;
		}
		if (Cloak.cloaked.contains(p.getName())){
			Cloak.removeCloak(p);
			return;
		}
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if ((!b.OverrideInt(p)) && (!checkTime(p, b)))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onInteract(p, e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
			// Util.sendMessage(p, ChatColor.GREEN + "You have used ability "
			// + b.getDisplay());
			// addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}



	public static void onJoin(Player p, PlayerJoinEvent e) {
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if (!checkTime(p, b))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p).intValue()) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onJoin(p, e))
			WarPoints.spendWarPoints(p, b.getCost());
//		addCooldown(p, b);
		Bukkit.getScheduler().runTask(
				RCWars.returnPlugin(),
				new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
						b.getDelay()));
	}

	public static void onLeave(Player p, PlayerQuitEvent e) {
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		b.onLeave(p, e);
		removeAllCooldown(p);
	}

	public static void onLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player))
			return;
		Player p = (Player) e.getEntity().getShooter();
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if (!checkTime(p, b))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onLaunch(e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
//			Util.sendMessage(p, ChatColor.GREEN + "You have used ability "
//					+ b.getDisplay());
//			addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}

	public static void onTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if ((!b.OverrideTpt(p)) && (!checkTime(p, b)))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onTeleport(e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
//			e.getPlayer()
//					.sendMessage(
//							ChatColor.GREEN + "You have used ability "
//									+ b.getDisplay());
//			addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}

	public static void onExplode(EntityExplodeEvent e) {
		if (!(e.getEntity() instanceof Projectile))
			return;
		Projectile proj = (Projectile) e.getEntity();
		if (!(proj.getShooter() instanceof Player))
			return;
		Player p = (Player) proj.getShooter();
		if (p == null)
			return;
		BaseAbility b = getAbility(p);
		if (b == null)
			return;
		if (!WarPoints.isLoaded(p.getUniqueId())) { // Player#getName() -> Player#getUniqueId()
			return;
		}
		if ((!b.OverrideTnt(p)) && (!checkTime(p, b)))
			return;
		if (b.getCost() > WarPoints.getWarPoints(p)) {
			Util.sendMessage(p, ChatColor.RED + "Not enough warpoints");
			return;
		}
		if (b.onExplode(e)) {
			if (b.getCost() != 0)
				WarPoints.spendWarPoints(p, b.getCost());
//			Util.sendMessage(p, ChatColor.GREEN + "You have used ability "
//					+ b.getDisplay());
//			addCooldown(p, b);
			Bukkit.getScheduler().runTask(
					RCWars.returnPlugin(),
					new AbilityCooldown(p, p.getInventory().getHeldItemSlot(),
							b.getDelay()));
		}
	}

	public static BaseAbility getAbility(Player p) {
		ItemStack item = p.getItemInHand();
		if (item == null)
			return null;
		ItemMeta im = item.getItemMeta();
		if (im == null)
			return null;
		return str2abil.get(ChatColor.stripColor(im.getDisplayName()));
	}
	
	public static boolean isBaseAbility(String s){
		if (s == null) return false;
		String sub = ChatColor.stripColor(s);
		if (str2abil.containsKey(sub)) return true;
		else if (str2abil.containsKey(sub.substring(1,sub.length() - 1))) return true;
		return false;
	}
	
	public static BaseAbility getUsedAbility(Player p) {
		ItemStack item = p.getItemInHand();
		if (item == null)
			return null;
		ItemMeta im = item.getItemMeta();
		if (im == null)
			return null;
		String d = ChatColor.stripColor(im.getDisplayName());
		if (d == null) return null;
		return str2abil.get(d.substring(1, d.length()));
	}

	public static boolean isUsedBaseAbility(String s) {
		if (s == null) return false;
		if (s.length() < 2) return false;
		String sub = ChatColor.stripColor(s);
		return str2abil.containsKey(sub.substring(1,sub.length() - 1));
	}
}