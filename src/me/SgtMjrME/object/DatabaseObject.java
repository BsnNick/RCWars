package me.SgtMjrME.object;

import me.SgtMjrME.Util;

public class DatabaseObject {
	
	public DatabaseObject(){
		s = "N/A";
		kills=0;
		deaths=0;
		wp=0;
	}
	
	public DatabaseObject(String name){
		s = name;
		kills = 0;
		deaths = 0;
		wp = 0;
	}
	
	public int get(int i){
		if (i == 0) return kills;
		if (i == 1) return deaths;
		if (i == 2) return wp;
		else return -1;
	}
	
	public void set(int i, int val){
		if (i == 0) kills = val;
		else if (i == 1) deaths = val;
		else if (i == 2) wp = val;
		else Util.sendLog("You went out of bounds on DBO, uhhh");
	}

	public String s;
	public int kills;
	public int deaths;
	public int wp;
}
