package com.example.utils;

public class Console {

    public static class DONE {
        public static void log(String message) {
            System.out.println(addGreenColor("[DONE]: ") + message);
        }
    }

    public static class INFO {
        public static void log(String message) {
            System.out.println(addBlueColor("[INFO]: ") + message);
        }
    }

    public static class WARN {
        public static void log(String message) {
            System.out.println(addOrangeColor("[WARN]: ") + message);
        }
    }

    public static class FAIL {
        public static void log(String message) {
            System.out.println(addRedColor("[FAIL]: ") + message);
        }
    }

    public static class LINE {
        public static void log(String message) {
            System.out.println(addGreyColor("[LINE]: [" + message + "]"));
        }
    }

    public static class HINT {
        public static void log(String message) {
            System.out.println(addYellowColor("[HINT]: ") + message);
        }
    }
    
    public static String addYellowColor(String text) {
        return "\u001B[33m" + text + removeColor();
    }

    public static String addRedColor(String text) {
        return "\u001B[31m" + text + removeColor();
    }

    public static String addGreyColor(String text) {
        return "\u001B[90m" + text + removeColor();
    }

    public static String addGreenColor(String text) {
        return "\u001B[32m" + text + removeColor();
    }

    public static String addOrangeColor(String text) {
        return "\u001B[38;5;208m" + text + removeColor();
    }

    public static String addBlueColor(String text) {
        return "\u001B[34m" + text + removeColor();
    }

    public static String removeColor() {
        return "\u001B[0m";
    }

    /* 
        Todo
        Iterate through the characters of a string. If a character is a special character, change it to gray
    */
}
