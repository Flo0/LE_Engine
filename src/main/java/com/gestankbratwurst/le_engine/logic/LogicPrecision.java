package com.gestankbratwurst.le_engine.logic;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 21.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@AllArgsConstructor
public enum LogicPrecision {

  MILLIS(1000L, TimeUnit.MILLISECONDS),
  MICROS(1000000L, TimeUnit.MICROSECONDS),
  NANOS(1000000000L, TimeUnit.NANOSECONDS);

  @Getter
  private final long secondsExponend;
  @Getter
  private final TimeUnit timeUnit;

}
