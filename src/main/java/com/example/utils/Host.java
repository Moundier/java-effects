package com.example.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {
    
    public static String getIpAddress() {
        try {
            // Get the local host's address and return it as a string
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Set Fallback value
            e.printStackTrace();
            return "192.168.0.6"; 
        }
    }
}
