package me.SgtMjrME.tasks;

import me.SgtMjrME.RCWars;

public class SpawnCheck implements Runnable {
	RCWars r;

	public SpawnCheck(RCWars r) {
		this.r = r;
	}

	public void run() {
		r.checkSpawn();
	}
}