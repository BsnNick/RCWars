package me.SgtMjrME.object;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.SgtMjrME.RCWars;
import me.SgtMjrME.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
	private String name;
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	private ArrayList<String> commands = new ArrayList<String>();
	private int cost;
	public static HashMap<String, Kit> kits = new HashMap<String, Kit>();

	public static void loadKits(RCWars pl) {
		File f = new File(pl.getDataFolder().getAbsolutePath() + "/Kits");
		String[] files = f.list();
		for (String s : files) {
			if (s.endsWith(".yml")) {
				f = new File(pl.getDataFolder().getAbsolutePath() + "/Kits/"
						+ s);
				try {
					kits.put(s.substring(0, s.length() - 4).toLowerCase(),
							new Kit(f));
				} catch (Exception localException) {
					Util.sendLog("Error loading kit " + s);
				}
			}
		}
	}

	Kit(File f) throws IOException, InvalidConfigurationException {
		YamlConfiguration cfg = new YamlConfiguration();
		cfg.load(f);
		name = cfg.getString("name");
		Util.sendLog(name + " loading");
		cost = cfg.getInt("cost");
		ConfigurationSection comConfig = cfg.getConfigurationSection("commands");
		if (comConfig != null){
			for (String s : comConfig.getKeys(false)){
				Util.sendLog(name + ":" + s);
				commands.add(comConfig.getString(s));
			}
		}
		if (!cfg.isConfigurationSection("items")) return;
		for(String cs : cfg.getConfigurationSection("items").getKeys(false)){
			/*ItemStack it = new ItemStack(cfg.getInt("items." + cs + ".itemid"),
					cfg.getInt("items." + cs + ".itemqty"),
					(short) cfg.getInt("items." + cs + ".itemdat"));*/
		    
		    ItemStack it = new ItemStack(Material.getMaterial(cfg.getString("items." + cs + ".itemid")), cfg.getInt("items." + cs + ".itemqty"));
		    
		    if (it.hasItemMeta() && it.getItemMeta() instanceof Damageable)
                ((Damageable)it.getItemMeta()).setDamage((short)cfg.getInt("items." + cs + ".itemdat"));
		    
			String s = cfg.getString("items." + cs + ".lore");
			if (s != null && s != ""){
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(s);
				ItemMeta itmeta = it.getItemMeta();
				itmeta.setLore(lore);
				it.setItemMeta(itmeta);
			}
			s = cfg.getString("items." + cs + ".name");
			if (s != null && s != ""){
				ItemMeta itmeta = it.getItemMeta();
				itmeta.setDisplayName(s);
				it.setItemMeta(itmeta);
			}
			s = cfg.getString("items." + cs + ".enchantments");
			if (s != null && s != ""){
				for(String ench : s.split(",")){
					Util.addEnchant(it, ench);
				}
			}
			items.add(it);
		}
	}

	public String getName() {
		return name;
	}

	public void addKit(Player p) {
		for (ItemStack item : items)
			p.getInventory().addItem(item);
		String name = p.getName();
		ConsoleCommandSender ccs = Bukkit.getConsoleSender();
		for (String s : commands) Bukkit.dispatchCommand(ccs, s.replace("%NAME%", name));
	}
	
	public void addKitCost(Player p){
		if (WarPoints.spendWarPoints(p, cost))
			addKit(p);
	}

	public static Kit getKit(String name) {
		if (name == null)
			return null;
		return kits.get(name.toLowerCase());
	}

	public static void listKits(CommandSender sender) {
		for (Kit k : kits.values()) {
			Util.sendMessage(sender, k.getName(), false);
			Util.sendMessage(sender, "Items", false);
			for (ItemStack item : k.items) {
				Util.sendMessage(sender, item.toString() + item.getEnchantments().toString(), false);
				if (item.getItemMeta().hasLore()) Util.sendMessage(sender, item.getItemMeta().getLore().toString(), false);
			}
			Util.sendMessage(sender, "Commands",false);
			for (String s : k.commands) Util.sendMessage(sender, "   "+s);
			Util.sendMessage(sender, "~~~~~~~~~~~~~~~~~~~~~~~~", false);
		}
	}
}