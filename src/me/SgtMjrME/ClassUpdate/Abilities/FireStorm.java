package me.SgtMjrME.ClassUpdate.Abilities;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class FireStorm extends BaseAbility {
	static HashSet<Material> airwater = Sets.newHashSet(new Material[] { Material.AIR, // 0 = Air
			Material.WATER, Material.LAVA }); // 8 = Flowing Water (Water), 9 = Still Water (Water), 10 = Flowing Lava (Lava), 11 = Still Lava (Lava)
	public final String disp;
	public final long delay;
	public final int cost;
	public final String desc;
	public final ItemStack item;

	public FireStorm(ConfigurationSection cs) {
		disp = ChatColor.translateAlternateColorCodes('&', cs.getString("display", "firestorm"));
		cost = cs.getInt("cost", 3);
		delay = cs.getLong("delay", 30000);
		desc = ChatColor.translateAlternateColorCodes('&', cs.getString("description", "(3 WP) Launches a firestorm"));
		item = new ItemStack(Material.NETHER_STAR, 1, (short) 0);
		String s = "FireStorm";
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(disp);
		if (s != null && s != ""){
			List<String> lore = new ArrayList<String>();
			lore.add(s);
			im.setLore(lore);
		}
		item.setItemMeta(im);
	}

	public boolean onDefend(Player p, EntityDamageByEntityEvent e) {
		if ((e.getEntity() instanceof Fireball)) {
			e.setCancelled(true);
		}
		return false;
	}

	public boolean OverrideDef(Player p) {
		return true;
	}
	
	public boolean OverrideTnt(Player p){
		return true;
	}

	public boolean onInteract(Player p, PlayerInteractEvent e) {
		Location l = p.getTargetBlock(airwater, 100).getLocation();

		Location temp = l.clone();
		temp.setY(temp.getY() + 25.0D);
		Vector dir = l.toVector().subtract(temp.toVector());
		for (int x = 0; x < 2; x++) {
			temp = l.clone();
			temp.setX(l.getX() + x * 4 - 2.0D);
			temp.setY(l.getY() + 25.0D + x);
			Fireball b = (Fireball) p.getWorld().spawnEntity(temp,
					EntityType.FIREBALL);
			b.setShooter(p);
			b.setDirection(dir);
			b.setYield(4.0F);
		}
		for (int z = 0; z < 2; z++) {
			temp = l.clone();
			temp.setZ(l.getZ() + z * 4 - 2.0D);
			temp.setY(l.getY() + 25.0D + 2.0D + z);
			Fireball b = (Fireball) p.getWorld().spawnEntity(temp,
					EntityType.FIREBALL);
			b.setShooter(p);
			b.setDirection(dir);
			b.setYield(4.0F);
		}
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