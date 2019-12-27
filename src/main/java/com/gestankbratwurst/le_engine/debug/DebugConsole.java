package com.gestankbratwurst.le_engine.debug;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 23.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class DebugConsole {

  private static GameConsole gameConsole;

  public static void create() {
      gameConsole = new GameConsole();
      log("Debug console enabled.");
  }

  public static void log(Object data) {
    if (gameConsole != null) {
      System.out.println(data.toString());
    }
  }

}
