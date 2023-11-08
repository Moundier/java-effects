package com.example.utils;

import javafx.application.Platform;

public class Defer {
  
  public static void platform(Runnable runnable) {
    Platform.runLater(runnable); // Defer JavaFX Thread
  }
}
