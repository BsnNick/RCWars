package me.SgtMjrME.tasks;

import me.SgtMjrME.RCWars;

public class timedExp implements Runnable {
	private RCWars pl;

	public timedExp(RCWars instance) {
		pl = instance;
	}

	public void run() {
		pl.timeGiveAll();
	}
}