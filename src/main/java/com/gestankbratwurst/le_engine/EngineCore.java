package com.gestankbratwurst.le_engine;

import com.gestankbratwurst.le_engine.audio.GameAudioController;
import com.gestankbratwurst.le_engine.data.DataManager;
import com.gestankbratwurst.le_engine.debug.DebugConsole;
import com.gestankbratwurst.le_engine.graphics.GameGraphicController;
import com.gestankbratwurst.le_engine.logic.GameLogicController;
import com.gestankbratwurst.le_engine.logic.LogicPrecision;
import com.gestankbratwurst.le_engine.startmenu.GameResolution;
import com.gestankbratwurst.le_engine.startmenu.StartMenu;
import com.gestankbratwurst.le_engine.startmenu.StartMenuData;
import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
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
public class EngineCore {

  @Getter
  private static int TPS = 20;
  private static EngineCore instance;

  public static EngineCore init(String gameName, int threadPoolSize, LogicPrecision logicPrecision, int TPS) {
    Preconditions.checkState(instance == null);
    EngineCore engineCore = new EngineCore(gameName, threadPoolSize, logicPrecision);
    EngineCore.TPS = TPS;
    instance = engineCore;
    return engineCore;
  }

  private EngineCore(String gameName, int threadPoolSize, LogicPrecision logicPrecision) {
    this.gameName = gameName;
    this.logicPrecision = logicPrecision;
    this.dataManager = new DataManager(this);
    mainWindow = new JFrame(gameName);
    gameGraphicController = new GameGraphicController();
    gameLogicController = new GameLogicController();
    gameAudioController = new GameAudioController();
    threadPoolExecutor = new ScheduledThreadPoolExecutor(threadPoolSize);
  }

  @Getter
  private final String gameName;
  private final LogicPrecision logicPrecision;
  private final ScheduledThreadPoolExecutor threadPoolExecutor;
  @Getter
  private final JFrame mainWindow;
  @Getter
  private final GameGraphicController gameGraphicController;
  @Getter
  private final GameLogicController gameLogicController;
  @Getter
  private final GameAudioController gameAudioController;
  @Getter
  private GameResolution gameResolution;
  @Getter
  private final DataManager dataManager;
  @Getter
  private boolean gameRunning = false;

  public void shutdown() {
    try {
      dataManager.saveData();
    } catch (IOException e) {
      e.printStackTrace();
    }
    threadPoolExecutor.shutdown();
    mainWindow.dispose();
  }

  public void registerRootKeyboardAction(ActionListener listener, KeyStroke keyStroke, int condition) {
    mainWindow.getRootPane().registerKeyboardAction(listener, keyStroke, condition);
  }

  public void registerRootKeyboardAction(ActionListener listener, KeyStroke keyStroke) {
    mainWindow.getRootPane().registerKeyboardAction(listener, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  public void addKeyListener(KeyListener listener) {
    mainWindow.addKeyListener(listener);
  }

  public void addComponentListener(ComponentListener listener) {
    mainWindow.addComponentListener(listener);
  }

  public void addContainerListener(ContainerListener listener) {
    mainWindow.addContainerListener(listener);
  }

  public void addMouseListener(MouseListener listener) {
    mainWindow.addMouseListener(listener);
  }

  public void addMouseMotionListener(MouseMotionListener listener) {
    mainWindow.addMouseMotionListener(listener);
  }

  public int getActiveThreadCount() {
    return threadPoolExecutor.getActiveCount();
  }

  public void openStartMenu(Consumer<EngineCore> postLogin) {
    StartMenu.open(this, postLogin);
  }

  public void setSizeAndOpen(StartMenuData startMenuData) {
    if (startMenuData.isUseConsole()) {
      DebugConsole.create();
    }
    this.gameResolution = startMenuData.getGameResolution();
    Dimension dimension = new Dimension(gameResolution.getWidth(), gameResolution.getHeight());
    mainWindow.getContentPane().setPreferredSize(dimension);
    mainWindow.pack();
    mainWindow.setResizable(false);
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.add(gameGraphicController);
    mainWindow.setVisible(true);
    setup();
  }

  private void setup() {
    long scheduleRate = logicPrecision.getSecondsExponend() / TPS;
    TimeUnit timeUnit = logicPrecision.getTimeUnit();
    threadPoolExecutor.scheduleAtFixedRate(gameLogicController, 0L, scheduleRate, timeUnit);
    threadPoolExecutor.scheduleAtFixedRate(gameGraphicController, 0L, 1L, TimeUnit.MILLISECONDS);
    try {
      dataManager.loadData();
    } catch (IOException e) {
      e.printStackTrace();
    }
    gameRunning = true;
  }

}
