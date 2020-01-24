package com.gestankbratwurst.le_engine.logic;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LE_Engine and was created at the 24.01.2020
 *
 * LE_Engine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class RepeatingTask extends TaskInstance {

  public RepeatingTask(GameTask task, long firstRun, long repeatingDelay) {
    super(task);
    this.repeatingDelay = repeatingDelay;
    this.nextRun = firstRun;
  }

  private final long repeatingDelay;
  private long nextRun;

  @Override
  public boolean shouldRun(long clockCount) {
    if (nextRun == clockCount) {
      nextRun = clockCount + repeatingDelay;
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldRepeat(long clockCount) {
    return true;
  }
}
