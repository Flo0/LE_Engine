package com.gestankbratwurst.le_engine.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LE_Engine and was created at the 28.01.2020
 *
 * LE_Engine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */

@AllArgsConstructor
public enum BufferStrategyValue {

  X4(4, "x4"),
  X3(3, "x3"),
  X2(2, "x2"),
  NONE(0, "None");

  @Getter
  private final int buffer;
  private final String display;

  @Override
  public String toString() {
    return display;
  }

}
