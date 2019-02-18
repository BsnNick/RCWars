package me.SgtMjrME.SiegeUpdate;

import org.bukkit.Location;
import org.bukkit.Material;

class BlockData {
	private Location loc;
    private Material mat;
	private org.bukkit.block.data.BlockData data;

	public BlockData(Location loc, Material material, org.bukkit.block.data.BlockData data) {
		this.loc = loc;
		this.mat = material;
		this.data = data;
	}
	
	public BlockData(Location loc) {
	    this.loc = loc;
	    this.mat = loc.getBlock().getType();
	    this.data = loc.getBlock().getBlockData();
	}
	
	public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public Material getType() {
        return mat;
    }

    public void setType(Material mat) {
        this.mat = mat;
    }

    public org.bukkit.block.data.BlockData getData() {
        return data;
    }

    public void setData(org.bukkit.block.data.BlockData blockData) {
        this.data = blockData;
    }
}