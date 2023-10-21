package com.example.utils;

public class Console {

    public static class DONE {
        public static void log(String message) {
            System.out.println("\u001B[32m [DONE] " + "\u001B[0m" + message ); // Green
        }
    }

    public static class INFO {
        public static void log(String message) {
            System.out.println("\u001B[33m [INFO] " + "\u001B[0m" + message ); // Yellow
        }
    }

    public static class WARN {
        public static void log(String message) {
            System.out.println("\u001B[31m [WARN] " + "\u001B[0m" + message ); // Red
        }
    }

    public static class LINE {
        public static void log(String message) {
            System.out.println("\u001B[90m [LINE] ["  + message +  "] \u001B[0m"); // Grey
        }
    }
}
