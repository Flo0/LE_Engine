package com.gestankbratwurst.le_engine;

import com.gestankbratwurst.le_engine.audio.GameAudioController;
import com.gestankbratwurst.le_engine.data.DataManager;
import com.gestankbratwurst.le_engine.debug.DebugConsole;
import com.gestankbratwurst.le_engine.graphics.BufferStrategyValue;
import com.gestankbratwurst.le_engine.graphics.GameGraphicController;
import com.gestankbratwurst.le_engine.logic.GameLogicController;
import com.gestankbratwurst.le_engine.logic.GameScheduler;
import com.gestankbratwurst.le_engine.logic.LogicPrecision;
import com.gestankbratwurst.le_engine.startmenu.GameResolution;
import com.gestankbratwurst.le_engine.startmenu.StartMenu;
import com.gestankbratwurst.le_engine.startmenu.StartMenuData;
import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    gameScheduler = new GameScheduler();
    gameLogicController = new GameLogicController(gameScheduler);
    gameAudioController = new GameAudioController();
    threadPoolExecutor = new ScheduledThreadPoolExecutor(threadPoolSize);
    graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    screenWidth = graphicsDevice.getDisplayMode().getWidth();
    screenHeight = graphicsDevice.getDisplayMode().getHeight();
  }

  @Getter
  private final String gameName;
  private final LogicPrecision logicPrecision;
  private final ScheduledThreadPoolExecutor threadPoolExecutor;
  @Getter
  private final JFrame mainWindow;
  @Getter
  private GameGraphicController gameGraphicController;
  @Getter
  private final GameLogicController gameLogicController;
  @Getter
  private final GameAudioController gameAudioController;
  @Getter
  private final GameScheduler gameScheduler;
  @Getter
  private GameResolution gameResolution;
  @Getter
  private final DataManager dataManager;
  @Getter
  private boolean gameRunning = true;
  @Getter
  private boolean consoleEnabled = false;
  @Getter
  private BufferStrategyValue bufferStrategyValue;
  @Getter
  private final int screenWidth;
  @Getter
  private final int screenHeight;
  @Getter
  private final GraphicsDevice graphicsDevice;

  public void shutdown() {
    try {
      dataManager.saveData();
    } catch (IOException e) {
      e.printStackTrace();
    }
    threadPoolExecutor.shutdown();
    mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
  }

  public void shutdownWithoutSave() {
    threadPoolExecutor.shutdown();
    mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
  }

  public void registerRootKeyboardAction(ActionListener listener, KeyStroke keyStroke, int condition) {
    mainWindow.getRootPane().registerKeyboardAction(listener, keyStroke, condition);
  }

  public void registerRootKeyboardAction(ActionListener listener, KeyStroke keyStroke) {
    mainWindow.getRootPane().registerKeyboardAction(listener, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  public void addKeyListener(KeyListener listener) {
    gameGraphicController.addKeyListener(listener);
  }

  public void addComponentListener(ComponentListener listener) {
    gameGraphicController.addComponentListener(listener);
  }

  public void addContainerListener(ContainerListener listener) {
    mainWindow.addContainerListener(listener);
  }

  public void addMouseListener(MouseListener listener) {
    gameGraphicController.addMouseListener(listener);
  }

  public void addMouseMotionListener(MouseMotionListener listener) {
    gameGraphicController.addMouseMotionListener(listener);
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
      this.consoleEnabled = true;
    }
    if (startMenuData.isFullScreenEnabled()) {
      this.gameResolution = GameResolution.fromScreen(screenWidth, screenHeight);
    } else {
      this.gameResolution = startMenuData.getGameResolution();
    }
    Dimension dimension = new Dimension(gameResolution.getWidth(), gameResolution.getHeight());
    this.bufferStrategyValue = startMenuData.getBufferStrategyValue();
    gameGraphicController = new GameGraphicController(this);
    gameGraphicController.setPreferredSize(dimension);
    gameGraphicController.setGameResolution(gameResolution);
    if (!startMenuData.isEnableWindowBar()) {
      mainWindow.setUndecorated(true);
    }
    if (startMenuData.isFullScreenEnabled()) {
      if (GameResolution.isSupported(screenWidth, screenHeight)) {
        gameGraphicController.setPreferredSize(dimension);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainWindow.setUndecorated(true);
        graphicsDevice.setFullScreenWindow(mainWindow);
      } else {
        JFrame errorFrame = new JFrame("Error");
        errorFrame.setSize(640, 480);
        errorFrame.setResizable(false);
        errorFrame.setLocationRelativeTo(null);
        JLabel label = new JLabel("Only true 16:9 resolutions are supported!");
        label.setFont(new Font("Arial", Font.BOLD, 32));
        errorFrame.add(label);
        errorFrame.setVisible(true);
      }
    }
    mainWindow.setResizable(false);
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.add(gameGraphicController);
    mainWindow.pack();
    mainWindow.setVisible(true);

    if (bufferStrategyValue != BufferStrategyValue.NONE) {
      gameGraphicController.createBufferStrategy(bufferStrategyValue.getBuffer());
    }

    setup();
  }

  private void setup() {
    long scheduleRate = logicPrecision.getSecondsExponend() / TPS;
    TimeUnit timeUnit = logicPrecision.getTimeUnit();
    threadPoolExecutor.execute(gameGraphicController);
    threadPoolExecutor.scheduleAtFixedRate(gameLogicController, 0L, scheduleRate, timeUnit);
    try {
      dataManager.loadData();
    } catch (IOException e) {
      e.printStackTrace();
    }
    gameRunning = true;
  }

}
