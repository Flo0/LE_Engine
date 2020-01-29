package com.gestankbratwurst.le_engine.startmenu;

import com.gestankbratwurst.le_engine.graphics.BufferStrategyValue;
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

public class StartMenuData {

  @Getter
  @Setter
  private GameResolution gameResolution;
  @Getter
  @Setter
  private boolean useConsole;
  @Getter
  @Setter
  private boolean enableWindowBar;
  @Getter
  @Setter
  private boolean fullScreenEnabled;
  @Getter
  @Setter
  private BufferStrategyValue bufferStrategyValue;

}
