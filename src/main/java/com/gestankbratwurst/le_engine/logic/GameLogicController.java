package com.gestankbratwurst.le_engine.logic;

import com.gestankbratwurst.le_engine.EngineCore;
import com.gestankbratwurst.le_engine.debug.DebugConsole;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumMap;
import java.util.Set;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 21.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class GameLogicController implements Runnable {

  public GameLogicController() {
    logicPriorityMap = new EnumMap<>(LogicPriority.class);
    for (LogicPriority logicPriority : LogicPriority.values()) {
      logicPriorityMap.put(logicPriority, new ObjectOpenHashSet<>());
    }
    logicTicks = 0L;
    lastCheck = System.nanoTime();
  }

  private final long[] timeEstimates = new long[]{0, 0, 0, 0, 0};
  private int timeEstimateIndex = 0;
  private long lastCheck;
  private long logicTicks;
  private final EnumMap<LogicPriority, ObjectSet<LogicElement>> logicPriorityMap;

  public double getTPS() {
    long sum = 0;
    for (int i = 0; i < timeEstimates.length; i++) {
      sum += timeEstimates[i];
    }
    sum /= timeEstimates.length;
    return (double) EngineCore.getTPS() / ((double) sum / 1E9D);
  }

  public void addLogicElement(LogicPriority priority, LogicElement logicElement) {
    logicPriorityMap.get(priority).add(logicElement);
  }

  public void removeLogicElement(LogicElement logicElement) {
    for (ObjectSet<LogicElement> logicSet : logicPriorityMap.values()) {
      logicSet.remove(logicElement);
    }
  }

  private void addTimeEstimate(long nanos) {
    timeEstimates[timeEstimateIndex] = nanos;
    timeEstimateIndex++;
    if (timeEstimateIndex == timeEstimates.length) {
      timeEstimateIndex = 0;
    }
  }

  @Override
  public void run() {
    try {
      logicTick();
    } catch (Exception e) {
      for (StackTraceElement trace : e.getStackTrace()) {
        DebugConsole.log(trace);
      }
    }
  }

  private void logicTick() {
    logicTicks++;
    if (logicTicks == EngineCore.getTPS()) {
      long current = System.nanoTime();
      long delta = current - lastCheck;
      lastCheck = current;
      logicTicks = 0;
      addTimeEstimate(delta);
    }
    for (LogicPriority priority : LogicPriority.values()) {
      Set<LogicElement> logicSet = logicPriorityMap.get(priority);
      for (LogicElement logicElement : logicSet) {
        logicElement.onTick();
      }
    }
  }

}
