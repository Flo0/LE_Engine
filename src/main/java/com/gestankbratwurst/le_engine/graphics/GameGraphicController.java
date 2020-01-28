package com.gestankbratwurst.le_engine.graphics;

import com.gestankbratwurst.le_engine.EngineCore;
import com.gestankbratwurst.le_engine.startmenu.GameResolution;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.EnumMap;
import java.util.concurrent.locks.LockSupport;
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
public class GameGraphicController extends Canvas implements Runnable {

  private static final int FRAME_TRACK_AMOUNT = 5;
  private static final long MS_PER_FPS_REFRESH = 1000L;

  public GameGraphicController(EngineCore engineCore) {
    this.engineCore = engineCore;
    this.setIgnoreRepaint(true);
    //this.setDoubleBuffered(true);
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
  private final EngineCore engineCore;
  @Getter
  @Setter
  private int fpsLimit = 60;
  @Getter
  @Setter
  private boolean fpsLimitEnabled = false;
  @Getter
  @Setter
  private boolean graphicsEnabled = false;
  @Getter
  @Setter
  private GameResolution gameResolution;

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

  public void putGraphicTask(String taskID, GTask task) {
    synchronized (graphicsQueue) {
      putGraphicTask(GraphicPriority.MEDIUM, taskID, task);
    }
  }

  public void removeGraphicTask(String taskID) {
    synchronized (graphicsQueue) {
      for (Object2ObjectOpenHashMap<String, GTask> map : graphicsQueue.values()) {
        map.remove(taskID);
      }
    }
  }

  private void render(Graphics graphics) {
    //GameResolution gameResolution = engineCore.getGameResolution();
    //BufferedImage buffer = new BufferedImage(gameResolution.getWidth(), gameResolution.getHeight(), BufferedImage.TYPE_INT_ARGB);
    //Graphics bufferGraphics = buffer.getGraphics();
    //VolatileImage buffer = this.createVolatileImage(gameResolution.getWidth(), gameResolution.getHeight());
    //graphics = this.getBufferStrategy().getDrawGraphics();
    synchronized (graphicsQueue) {
      for (GraphicPriority priority : GraphicPriority.values()) {
        for (GTask task : graphicsQueue.get(priority).values()) {
          task.accept(graphics);
        }
      }
    }
    //graphics.drawImage(buffer, 0, 0, null);
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
      graphics.setColor(fpsColor);
      graphics.setFont(fpsFont);
      graphics.drawString("" + (int) fps, 6, 23);
    }
  }

  public void customRepaint() {
    BufferStrategy bs = this.getBufferStrategy();
    Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();

    try {
      g2 = (Graphics2D) bs.getDrawGraphics();
      this.render(g2);
    } finally {
      g2.dispose();
    }
    bs.show();
  }

  @Override
  public void run() {
    //VolatileImage volatileImage = this.createVolatileImage(gameResolution.getWidth(), gameResolution.getHeight());
    while (engineCore.isGameRunning()) {
      long prePaint = System.nanoTime();
      this.customRepaint();
      if (fpsLimitEnabled) {
        long paintTimeMicros = (System.nanoTime() - prePaint) / 1000L;
        long microsToSleep = 1000000L / fpsLimit - paintTimeMicros;
        sleepMicros(microsToSleep);
      }
    }
  }

  private void sleepMicros(long microsToSleep) {
    LockSupport.parkNanos(microsToSleep * 1000);
  }

}
