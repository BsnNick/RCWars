package me.SgtMjrME.tasks;

import me.SgtMjrME.RCWars;

public class RunCheck implements Runnable {
	private final RCWars p;

	public RunCheck(RCWars plugin) {
		p = plugin;
	}

	public void run() {
		p.checkPlayerBase();
	}
}