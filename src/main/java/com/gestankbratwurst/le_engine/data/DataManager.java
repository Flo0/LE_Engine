package com.gestankbratwurst.le_engine.data;

import com.gestankbratwurst.le_engine.EngineCore;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.function.Consumer;
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
public class DataManager {

  public DataManager(EngineCore engineCore) {
    dataSaverSet = Sets.newHashSet();
    dataLoaderSet = Sets.newHashSet();
    gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    File root = null;
    try {
      root = new File(EngineCore.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    rootFolder = root;
    rootFolder.mkdirs();
    this.mainDataFile = new File(rootFolder, engineCore.getGameName().replace(" ", "_") + ".json");
  }

  @Getter
  private final File rootFolder;
  @Getter
  private final File mainDataFile;
  private final Set<Consumer<JsonObject>> dataSaverSet;
  private final Set<Consumer<JsonObject>> dataLoaderSet;
  private final Gson gson;

  public void loadData() throws IOException {
    if (!mainDataFile.exists()) {
      return;
    }
    InputStreamReader isr = new InputStreamReader(new FileInputStream(mainDataFile));
    StringBuilder builder = new StringBuilder();
    int read;
    while ((read = isr.read()) != -1) {
      builder.append((char) read);
    }
    JsonObject loadData = gson.fromJson(builder.toString(), JsonObject.class);
    for (Consumer<JsonObject> loadConsumer : dataLoaderSet) {
      loadConsumer.accept(loadData);
    }
  }

  public void saveData() throws IOException {
    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(rootFolder), "UTF-8");
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    JsonObject saveData = new JsonObject();
    for (Consumer<JsonObject> saveConsumer : dataSaverSet) {
      saveConsumer.accept(saveData);
    }
    osw.write(gson.toJson(saveData));
    osw.close();
  }

  public void registerDataSaver(Consumer<JsonObject> dataSaver) {
    dataSaverSet.add(dataSaver);
  }

  public void registerDataLoader(Consumer<JsonObject> dataLoader) {
    dataLoaderSet.add(dataLoader);
  }

}
