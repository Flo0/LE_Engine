package com.gestankbratwurst.le_engine.startmenu;

import com.gestankbratwurst.le_engine.EngineCore;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

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
    this.setSize(640, 240);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.startMenuData = new StartMenuData();
    startMenuData.setGameResolution(GameResolution.LOW);
    JComboBox<GameResolution> resolutionChoose = new JComboBox(GameResolution.values());
    JCheckBox debugCheck = new JCheckBox("Console enabled", false);
    JCheckBox barCheck = new JCheckBox("Borderless", false);
    this.add(barCheck);
    resolutionChoose.setBounds(240, this.getHeight() / 3, 220, 28);
    debugCheck.setBounds(160, this.getHeight() / 3 + 32, 160, 28);
    barCheck.setBounds(160, this.getHeight() / 3 + 64, 160, 28);
    JButton startButton = new JButton("Start");
    startButton.setBounds(160, this.getHeight() / 3, 72, 28);
    startButton.addActionListener(event -> {
      GameResolution res = (GameResolution) resolutionChoose.getSelectedItem();
      startMenuData.setGameResolution(res);
      startMenuData.setUseConsole(debugCheck.isSelected());
      startMenuData.setEnableWindowBar(!barCheck.isSelected());
      engineCore.setSizeAndOpen(startMenuData);
      postLogin.accept(engineCore);
      menuInstance.setVisible(false);
      dispose();
    });
    this.setLayout(null);
    this.add(startButton);
    this.add(resolutionChoose);
    this.add(debugCheck);
  }

  private final JFrame menuInstance;
  private final StartMenuData startMenuData;

}
