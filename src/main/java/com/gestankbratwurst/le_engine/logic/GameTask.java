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
public class GameTask implements Runnable {

  public GameTask(int taskID, Runnable runnable) {
    this.taskID = taskID;
    this.runnable = runnable;
  }

  protected final int taskID;
  private final Runnable runnable;

  public void stop() {
    GameScheduler.getInstance().cancelTask(this.taskID);
  }

  @Override
  public void run() {
    this.runnable.run();
  }

}