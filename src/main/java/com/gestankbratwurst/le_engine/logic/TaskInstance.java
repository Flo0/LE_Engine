package com.gestankbratwurst.le_engine.logic;

import lombok.AllArgsConstructor;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LE_Engine and was created at the 24.01.2020
 *
 * LE_Engine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@AllArgsConstructor
public abstract class TaskInstance implements Runnable {

  protected final GameTask task;

  @Override
  public void run() {
    task.run();
  }

  public abstract boolean shouldRun(long clockCount);
  public abstract boolean shouldRepeat(long clockCount);

}