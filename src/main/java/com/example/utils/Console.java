package com.example.utils;


public class Console {

    public static class DONE {
        public static void log(String message) {
            System.out.println(addGreenColor("[DONE]: ") + message); // Red
        }
    }

    public static class INFO {
        public static void log(String message) {
            System.out.println(addBlueColor("[INFO]: ") + message); // Print info message in blue
        }
    }

    public static class WARN {
        public static Void log(String message) {
            System.out.println(addOrangeColor("[WARN]: ") + message); // Orange
            return null;
        }
    }

    public static class FAIL {
        public static Void log(String message) {
            System.out.println(addRedColor("[FAIL]: ") + message); // Red
            return null;
        }
    }

    public static class LINE {
        public static Void log(String message) {
            System.out.println(addGreyColor("[LINE]:") + addGreyColor(" [" + message + "]")); // Grey
            return null;
        }
    }

    public static String addRedColor(String text) {
        return "\u001B[31m" + text + removeColor(); // ok
    }

    public static String addGreyColor(String text) {
        return "\u001B[90m" + text + removeColor(); // ok
    }

    public static String addGreenColor(String text) {
        return "\u001B[32m" + text + removeColor(); // ok
    }

    public static String addOrangeColor(String text) {
        return "\u001B[38;5;208m" + text + removeColor();
    }

    public static String addBlueColor(String text) {
        return "\u001B[34m" + text + removeColor(); // Adds blue color
    }

    public static String removeColor() {
        return "\u001B[0m";
    }

    /* 
        Todo
        Iterate through the characters of a string. If a character is a special character, change it to gray
    */

}
