package me.SgtMjrME.tasks;

import java.util.HashMap;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import me.SgtMjrME.UtilColorParticle;

public class CosmeticHandler extends BukkitRunnable
{
	public static HashMap<TNTPrimed, String> _tnt = new HashMap<TNTPrimed, String>();
	
	@Override
	public void run()
	{
		for (TNTPrimed tnt : _tnt.keySet())
		{
			if (tnt.isDead() || !tnt.isValid() || tnt == null)
			{
				_tnt.remove(tnt);
				cancel();
			}
			
			switch (_tnt.get(tnt))
			{
			case "orc":
				UtilColorParticle.RED_DUST.send(tnt.getLocation(), 255, 85, 85);
				break;
			case "owarf":
				UtilColorParticle.RED_DUST.send(tnt.getLocation(), 255, 170, 0);
				break;
			case "human":
				UtilColorParticle.RED_DUST.send(tnt.getLocation(), 85, 85, 255);
				break;
			case "elf":
				UtilColorParticle.RED_DUST.send(tnt.getLocation(), 85, 255, 85);
				break;
			default:
				UtilColorParticle.RED_DUST.send(tnt.getLocation(), 0, 0, 0);
				break;
			}
		}
	}
}