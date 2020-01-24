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
public class DelayedTask extends TaskInstance {

  public DelayedTask(GameTask task, long exectionTime) {
    super(task);
    this.exectionTime = exectionTime;
  }

  private final long exectionTime;

  @Override
  public boolean shouldRun(long clockCount) {
    return clockCount == exectionTime;
  }

  @Override
  public boolean shouldRepeat(long clockCount) {
    return clockCount != exectionTime;
  }
}
