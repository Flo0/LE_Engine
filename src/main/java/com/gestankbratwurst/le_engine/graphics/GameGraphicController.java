package com.gestankbratwurst.le_engine.graphics;

import com.gestankbratwurst.le_engine.debug.DebugConsole;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.EnumMap;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 21.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class GameGraphicController extends JPanel implements Runnable {

  private static final int FRAME_TRACK_AMOUNT = 5;
  private static final long MS_PER_FPS_REFRESH = 1000L;

  public GameGraphicController() {
    this.setDoubleBuffered(true);
    graphicsQueue = new EnumMap<>(GraphicPriority.class);
    for (GraphicPriority priority : GraphicPriority.values()) {
      graphicsQueue.put(priority, new Object2ObjectOpenHashMap<>());
    }
    for (int i = 0; i < FRAME_TRACK_AMOUNT; i++) {
      fpsEstimates[i] = 60L;
    }
    this.fpsFont = new Font("SansSerif", Font.BOLD, 26);
  }

  private long framesDrawn = 0;
  private long lastFrameCheck = System.currentTimeMillis();
  private double lastEstimate = 0D;
  private long lastCheck = System.currentTimeMillis();
  @Getter
  @Setter
  private final Font fpsFont;
  private final EnumMap<GraphicPriority, Object2ObjectOpenHashMap<String, GTask>> graphicsQueue;
  private final long[] fpsEstimates = new long[FRAME_TRACK_AMOUNT];
  private int estimateIndex = 0;
  @Getter
  @Setter
  private boolean internalFpsDrawerEnabled = false;
  @Getter
  @Setter
  private Color fpsColor = Color.BLACK;

  public double getFPS() {
    if (System.currentTimeMillis() - lastCheck >= MS_PER_FPS_REFRESH) {
      lastCheck = System.currentTimeMillis();
      long sum = 0;
      synchronized (fpsEstimates) {
        for (int i = 0; i < fpsEstimates.length; i++) {
          sum += fpsEstimates[i];
        }
      }
      lastEstimate = (double) sum / (double) FRAME_TRACK_AMOUNT;
    }
    return lastEstimate;
  }

  public void putGraphicTask(GraphicPriority priority, String taskID, GTask task) {
    synchronized (graphicsQueue) {
      graphicsQueue.get(priority).put(taskID, task);
    }
  }

  public void putGraphicTask(GTask task, String taskID) {
    putGraphicTask(GraphicPriority.MEDIUM, taskID, task);
  }

  public void removeGraphicTask(String taskID) {
    synchronized (graphicsQueue) {
      for (Object2ObjectOpenHashMap<String, GTask> map : graphicsQueue.values()) {
        map.remove(taskID);
      }
    }
  }

  @Override
  public void paint(Graphics graphics) {
    synchronized (graphicsQueue) {
      for (GraphicPriority priority : GraphicPriority.values()) {
        for (GTask task : graphicsQueue.get(priority).values()) {
          task.accept(graphics);
        }
      }
    }
    framesDrawn++;
    if (System.currentTimeMillis() - lastFrameCheck >= 1000L) {
      lastFrameCheck = System.currentTimeMillis();
      synchronized (fpsEstimates) {
        fpsEstimates[estimateIndex] = framesDrawn;
      }
      estimateIndex++;
      if (estimateIndex == fpsEstimates.length) {
        estimateIndex = 0;
      }
      framesDrawn = 0;
    }
    if (internalFpsDrawerEnabled) {
      double fps = getFPS();
      graphics.setColor(Color.BLACK);
      graphics.setFont(fpsFont);
      graphics.drawString("" + (int) fps, 6, 23);
    }
  }

  @Override
  public void run() {
    try {
      repaint();
    } catch (Exception e) {
      for (StackTraceElement trace : e.getStackTrace()) {
        DebugConsole.log(trace);
      }
    }
  }

}
