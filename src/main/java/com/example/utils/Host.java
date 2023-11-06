package com.example.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

  public static InetAddress fetchLocalIP() {
    try {
      // Get the local host's address and return it as an InetAddress
      String inetAddress = InetAddress.getLocalHost().getHostAddress();
      return InetAddress.getByName(inetAddress);
    } catch (UnknownHostException e) {
      // Should return fallback of "192.168.0.6"
      e.printStackTrace();
      return null;
    }
  }
}
