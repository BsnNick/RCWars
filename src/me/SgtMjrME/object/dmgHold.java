package me.SgtMjrME.object;

class dmgHold
{
  public Long time;
  public String prevDamager;

  public dmgHold(long currentTimeMillis, String prev)
  {
    time = currentTimeMillis;
    prevDamager = prev;
  }
}