package me.SgtMjrME;
 
import org.bukkit.Location;

import me.SgtMjrME.UtilParticles.OrdinaryColor;
 
public enum UtilColorParticle
{
    MOB_SPELL("MOB_SPELL"), MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT"), RED_DUST("RED_DUST");
	
    private UtilColorParticle(String name)
    {
        this.name = name;
    }
    
    String name;
    
    public void send(Location location, int r, int g, int b)
    {
    	UtilParticles.REDSTONE.display(new OrdinaryColor(r, g, b), location, 64);
    }
}