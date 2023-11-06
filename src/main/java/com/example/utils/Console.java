package com.example.utils;

public class Console {

  public static void log(String prefix, String message, String color) {
    System.out.println(addColor("[" + prefix + "]: " + message, color));
  }

  public static class DONE {
    public static void log(String message) {
      Console.log("DONE", message, "green");
    }
  }

  public static class INFO {
    public static void log(String message) {
      Console.log("INFO", message, "blue");
    }
  }

  public static class WARN {
    public static void log(String message) {
      Console.log("WARN", message, "magenta");
    }
  }

  public static class FAIL {
    public static void log(String message) {
      Console.log("FAIL", message, "red");
    }
  }

  public static class LINE {
    public static void log(String message) {
      Console.log("LINE", message, "grey");
    }
  }

  public static class HINT {
    public static void log(String message) {
      Console.log("HINT", message, "cyan");
    }
  }

  public static String addColor(String text, String color) {
    String colorCode = getColorCode(color);
    return colorCode + text + removeColor();
  }

  public static String getColorCode(String color) {
    switch (color) {
      case "black":
        return "\u001B[30m";
      case "red":
        return "\u001B[31m";
      case "green":
        return "\u001B[32m";
      case "yellow":
        return "\u001B[33m";
      case "blue":
        return "\u001B[34m";
      case "magenta":
        return "\u001B[35m";
      case "cyan":
        return "\u001B[36m";
      case "white":
        return "\u001B[37m";
      case "grey":
        return "\u001B[90m";
      default:
        return "";
    }
  }

  public static String removeColor() {
    return "\u001B[0m";
  }
}
