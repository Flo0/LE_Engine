package com.gestankbratwurst.le_engine.audio;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 22.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class GameAudioController {

  public GameAudioController() {
    audioClips = Maps.newHashMap();
  }

  public synchronized Clip createClip(AudioInputStream audioIn, String clipName) {
    Clip clip = null;
    try {
      clip = AudioSystem.getClip();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
    try {
      clip.open(audioIn);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    audioClips.put(clipName, clip);
    return clip;
  }

  public synchronized Clip createClip(File soundFile, String clipName) {
    Clip clip = null;
    try {
      clip = createClip(AudioSystem.getAudioInputStream(soundFile), clipName);
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return clip;
  }

  public synchronized Clip createClip(InputStream inputStream, String clipName) {
    Clip clip = null;
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
      clip = createClip(audioInputStream, clipName);
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return clip;
  }

  public synchronized Clip createClip(URL url, String clipName) {
    Clip clip = null;
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      clip = createClip(audioInputStream, clipName);
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return clip;
  }

  public synchronized Clip getClip(String clipName) {
    return audioClips.get(clipName);
  }

  private final Map<String, Clip> audioClips;

}
