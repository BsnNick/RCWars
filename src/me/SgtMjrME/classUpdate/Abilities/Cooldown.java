package me.SgtMjrME.classUpdate.Abilities;

class Cooldown {
	BaseAbility a;
	Long time;

	Cooldown(BaseAbility ba) {
		a = ba;
		time = Long.valueOf(System.currentTimeMillis());
	}
}