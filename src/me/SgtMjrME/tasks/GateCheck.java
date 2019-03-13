package me.SgtMjrME.tasks;

import me.SgtMjrME.RCWars;

public class GateCheck implements Runnable {
	private final RCWars p;

	public GateCheck(RCWars plugin) {
		p = plugin;
	}

	public void run() {
		p.switchGates();
	}
}