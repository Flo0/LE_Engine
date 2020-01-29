package com.gestankbratwurst.le_engine.startmenu;

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
public enum GameResolution {

  LOW(640, 360),
  LOW_PLUS(1024, 576),
  HD(1280, 720),
  HD_PLUS(1536, 864),
  FULL_HD(1920, 1080),
  FULL_HD_PLUS(2176, 1224),
  DHD(2560, 1440),
  DHD_PLUS(3072, 1728),
  UHD(3840, 2160);

  @Getter
  private final int width;
  @Getter
  private final int height;

  public static GameResolution fromScreen(int width, int height) {
    for (GameResolution resolution : GameResolution.values()) {
      if (resolution.width == width) {
        if (resolution.height == height) {
          return resolution;
        }
      }
    }
    return null;
  }

  public static boolean isSupported(int width, int height) {
    for (GameResolution resolution : GameResolution.values()) {
      if (resolution.width == width) {
        if (resolution.height == height) {
          return true;
        }
      }
    }
    return false;
  }

  public String getDisplay() {
    return width + "x" + height;
  }

  @Override
  public String toString() {
    return super.toString() + " (" + getDisplay() + ")";
  }

}
