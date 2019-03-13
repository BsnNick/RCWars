package me.SgtMjrME.tasks;

import me.SgtMjrME.RCWars;

public class TimedExp implements Runnable {
	private RCWars pl;

	public TimedExp(RCWars instance) {
		pl = instance;
	}

	public void run() {
		pl.timeGiveAll();
	}
}