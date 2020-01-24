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
public class SimpleTask extends TaskInstance {

  public SimpleTask(GameTask task) {
    super(task);
  }

  @Override
  public boolean shouldRun(long clockCount) {
    return true;
  }

  @Override
  public boolean shouldRepeat(long clockCount) {
    return false;
  }

}