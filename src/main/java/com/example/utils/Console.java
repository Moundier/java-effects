package com.example.utils;


public class Console {

    public static class DONE {
        public static void log(String message) {
            System.out.println(addGreenColor("[DONE]: ") + message); // Red
        }
    }

    public static class INFO {
        public static Void log(String message) {
            System.out.println(addYellowColor("[INFO]: ") + message); // Red
            return null;
        }
    }

    public static class WARN {
        public static Void log(String message) {
            System.out.println(addRedColor("[WARN]: ") + message); // Red
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

    public static String addYellowColor(String text) {
        return "\u001B[33m" + text + removeColor();
    }

    public static String removeColor() {
        return "\u001B[0m";
    }

    /* 
        Todo
        Iterate through the characters of a string. If a character is a special character, change it to gray
    */
}
