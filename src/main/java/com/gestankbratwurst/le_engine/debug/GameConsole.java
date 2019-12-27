package com.gestankbratwurst.le_engine.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of TextEngine and was created at the 25.12.2019
 *
 * TextEngine can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class GameConsole extends WindowAdapter implements WindowListener, ActionListener, Runnable {


  private JFrame frame;
  private JTextArea textArea;
  private Thread reader;
  private Thread reader2;
  private boolean quit;

  private final PipedInputStream pin = new PipedInputStream();
  private final PipedInputStream pin2 = new PipedInputStream();

  Thread errorThrower;

  public GameConsole() {
    // create all components and add them
    frame = new JFrame("Console");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
    int x = (int) (frameSize.width / 2);
    int y = (int) (frameSize.height / 2);
    frame.setBounds(x, y, frameSize.width, frameSize.height);

    textArea = new JTextArea();
    textArea.setEditable(false);
    JButton button = new JButton("clear");

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
    frame.getContentPane().add(button, BorderLayout.SOUTH);
    frame.setVisible(true);

    frame.addWindowListener(this);
    button.addActionListener(this);

    try {
      PipedOutputStream pout = new PipedOutputStream(this.pin);
      System.setOut(new PrintStream(pout, true));
    } catch (java.io.IOException io) {
      textArea.append("Couldn't redirect STDOUT to this console\n" + io.getMessage());
    } catch (SecurityException se) {
      textArea.append("Couldn't redirect STDOUT to this console\n" + se.getMessage());
    }

    try {
      PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
      System.setErr(new PrintStream(pout2, true));
    } catch (java.io.IOException io) {
      textArea.append("Couldn't redirect STDERR to this console\n" + io.getMessage());
    } catch (SecurityException se) {
      textArea.append("Couldn't redirect STDERR to this console\n" + se.getMessage());
    }

    quit = false; // signals the Threads that they should exit

    // Starting two seperate threads to read from the PipedInputStreams
    reader = new Thread(this);
    reader.setDaemon(true);
    reader.start();

    reader2 = new Thread(this);
    reader2.setDaemon(true);
    reader2.start();
  }

  public synchronized void windowClosed(WindowEvent evt) {
    quit = true;
    this.notifyAll();
    try {
      reader.join(1000);
      pin.close();
    } catch (Exception e) {
    }
    try {
      reader2.join(1000);
      pin2.close();
    } catch (Exception e) {
    }
    System.exit(0);
  }

  public synchronized void windowClosing(WindowEvent evt) {
    frame.setVisible(false);
    frame.dispose();
  }

  public synchronized void actionPerformed(ActionEvent evt) {
    // TODO commands
    textArea.setText("");
  }

  public synchronized void run() {
    try {
      while (Thread.currentThread() == reader) {
        try {
          this.wait(100);
        } catch (InterruptedException ie) {
        }
        if (pin.available() != 0) {
          String input = this.readLine(pin);
          textArea.append(input);
        }
        if (quit) {
          return;
        }
      }

      while (Thread.currentThread() == reader2) {
        try {
          this.wait(100);
        } catch (InterruptedException ie) {
        }
        if (pin2.available() != 0) {
          String input = this.readLine(pin2);
          textArea.append(input);
        }
        if (quit) {
          return;
        }
      }
    } catch (Exception e) {
      textArea.append("Error: " + e);
    }

  }

  public synchronized String readLine(PipedInputStream in) throws IOException {
    String input = "";
    do {
      int available = in.available();
      if (available == 0) {
        break;
      }
      byte b[] = new byte[available];
      in.read(b);
      input = input + new String(b, 0, b.length);
    } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
    return input;
  }


}
