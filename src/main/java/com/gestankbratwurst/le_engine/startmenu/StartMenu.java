package com.gestankbratwurst.le_engine.startmenu;

import com.gestankbratwurst.le_engine.EngineCore;
import com.gestankbratwurst.le_engine.graphics.BufferStrategyValue;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 21.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class StartMenu extends JFrame {

  public static void open(EngineCore engineCore, Consumer<EngineCore> postLogin) {
    new StartMenu(engineCore, postLogin).setVisible(true);
  }

  private StartMenu(EngineCore engineCore, Consumer<EngineCore> postLogin) {
    menuInstance = this;
    this.setTitle("Start Menu");
    this.setSize(640, 360);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.startMenuData = new StartMenuData();
    startMenuData.setGameResolution(GameResolution.LOW);

    JComboBox<GameResolution> resolutionChoose = new JComboBox(GameResolution.values());
    JComboBox<GameResolution> bufferChoose = new JComboBox(BufferStrategyValue.values());
    bufferChoose.setSelectedItem(BufferStrategyValue.X2);
    JCheckBox debugCheck = new JCheckBox("Console enabled", false);
    JCheckBox barCheck = new JCheckBox("Borderless", false);
    JCheckBox fullscreenCheck = new JCheckBox("Fullscreen", false);
    JLabel bufferLabel = new JLabel("Buffer Strategy");

    int baseHeight = this.getHeight() / 4;

    JButton startButton = new JButton("Start");
    startButton.setBounds(160, baseHeight, 72, 28);
    resolutionChoose.setBounds(240, baseHeight, 220, 28);
    debugCheck.setBounds(160, baseHeight + 32, 160, 28);
    barCheck.setBounds(160, baseHeight + 64, 160, 28);
    fullscreenCheck.setBounds(160, baseHeight + 96, 160, 28);
    bufferLabel.setBounds(160, baseHeight + 128, 160, 28);
    bufferChoose.setBounds(255, baseHeight + 128, 205, 28);

    startButton.addActionListener(event -> {
      GameResolution res = (GameResolution) resolutionChoose.getSelectedItem();
      BufferStrategyValue bufferStrategyValue = (BufferStrategyValue) bufferChoose.getSelectedItem();
      startMenuData.setGameResolution(res);
      startMenuData.setUseConsole(debugCheck.isSelected());
      startMenuData.setEnableWindowBar(!barCheck.isSelected());
      startMenuData.setBufferStrategyValue(bufferStrategyValue);
      startMenuData.setFullScreenEnabled(fullscreenCheck.isSelected());
      engineCore.setSizeAndOpen(startMenuData);
      postLogin.accept(engineCore);
      menuInstance.setVisible(false);
      dispose();
    });

    this.add(fullscreenCheck);
    this.add(resolutionChoose);
    this.add(bufferChoose);
    this.add(barCheck);
    this.add(debugCheck);
    this.add(bufferLabel);
    this.add(startButton);

    this.setLayout(null);
    this.setLocationRelativeTo(null);

  }

  private final JFrame menuInstance;
  private final StartMenuData startMenuData;

}
