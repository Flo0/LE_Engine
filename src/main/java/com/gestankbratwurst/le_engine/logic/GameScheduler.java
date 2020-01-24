package com.gestankbratwurst.le_engine.logic;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AccessLevel;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LE_Engine and was created at the 24.01.2020
 *
 * LE_Engine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class GameScheduler implements LogicElement {

  @Getter(AccessLevel.PROTECTED)
  private static GameScheduler instance;

  public GameScheduler() {
    instance = this;
    this.scheduledTasks = new Int2ObjectOpenHashMap<>();
  }

  private final Int2ObjectMap<TaskInstance> scheduledTasks;
  private long gameTickCount = 0;
  private int threadIDs = 0;

  public GameTask scheduleRepeatingTask(Runnable task, long initialDelay, long repeatingDelay) {
    GameTask gameTask = new GameTask(threadIDs++, task);
    synchronized (scheduledTasks) {
      scheduledTasks.put(gameTask.taskID, new RepeatingTask(gameTask, gameTickCount + initialDelay, repeatingDelay));
    }
    return gameTask;
  }

  public GameTask runTaskLater(Runnable task, long initialDelay) {
    GameTask gameTask = new GameTask(threadIDs++, task);
    synchronized (scheduledTasks) {
      scheduledTasks.put(gameTask.taskID, new DelayedTask(gameTask, gameTickCount + initialDelay));
    }
    return gameTask;
  }

  public GameTask runTask(Runnable task) {
    GameTask gameTask = new GameTask(threadIDs++, task);
    synchronized (scheduledTasks) {
      scheduledTasks.put(gameTask.taskID, new SimpleTask(gameTask));
    }
    return gameTask;
  }

  public void cancelTask(int id) {
    scheduledTasks.remove(id);
  }

  @Override
  public void onTick() {
    gameTickCount++;
    IntSet removers = new IntOpenHashSet();
    for (TaskInstance taskInstance : scheduledTasks.values()) {
      if (taskInstance.shouldRun(gameTickCount)) {
        taskInstance.run();
      }
      if (!taskInstance.shouldRepeat(gameTickCount)) {
        removers.add(taskInstance.task.taskID);
      }
    }
    for (int removerID : removers) {
      cancelTask(removerID);
    }
  }

}