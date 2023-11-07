package com.example.service;

import java.net.*;
import java.util.*;

import com.example.model.User;
import com.example.utils.JsonUser;
import com.example.utils.LocalHost;
import com.example.utils.Console.DONE;
import com.example.utils.Console.INFO;
import com.example.utils.Console.LINE;
import com.example.utils.Console.WARN;
import com.example.utils.Console.FAIL;
import com.example.utils.Console.HINT;
import com.example.view.MenuView;

public class Broadcaster {

  private final int RADAR_PORT = 8084;
  private final String BROADCAST_IP = "255.255.255.255";

  private User user;
  private Set<User> online = new HashSet<>();
  private MenuView menuView;

  public Broadcaster(User user, MenuView menuView) {
    this.user = user;
    this.menuView = menuView;
    this.start();
  }

  Runnable sendRadarMessage = () -> {
    try (DatagramSocket socket = new DatagramSocket()) {
      while (true) {
        DONE.log("sendRadarMessage");
        String radarMessage = JsonUser.serializeUser(user);
        byte[] sendData = radarMessage.getBytes();
        InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP);
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress, RADAR_PORT);
        socket.send(packet);
        Thread.sleep(5000);
      }
    } catch (Exception e) {
      FAIL.log("Server bind error on sending");
      e.printStackTrace();
    }
  };

  private int signal = 0;

  public int computed() {
    return signal = signal + 1;
  }

  Runnable receiveRadarMessages = () -> {
    try (DatagramSocket socket = new DatagramSocket(RADAR_PORT)) {
      while (true) {
        WARN.log("Counter: " + computed());
        LINE.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        byte[] buffer = new byte[4096]; // 8192
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String radarMessage = new String(packet.getData(), 0, packet.getLength());
        HINT.log("Found: " + radarMessage);
        User receivedUser = JsonUser.deserializeUser(radarMessage);
        processRadarMessage(receivedUser);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  };

  void processRadarMessage(User receivedUser) {

    synchronized (online) {
      // Update user timestmap
      for (User user : online) {
        if (user.equals(receivedUser)) {
          user.updateTimestap();
          user.updateStatus(receivedUser.getStatus());
        }
      }

      Boolean isNotFound = (online.isEmpty());
      Boolean isLocalHost = (receivedUser.getInetAddress().equals(LocalHost.getByName()));

      if (isNotFound)
        FAIL.log("No users online.");
      else {
        INFO.log("Online " + online);
      }

      // boolean others = !isCurrent; // exclude myself in online
      boolean includeSelf = !isLocalHost || isLocalHost; // include myself in online

      if (includeSelf) {
        this.menuView.addUsers(online);
        online.add(receivedUser);
        online.notify();
      }
    }
  }

  Runnable removeInactiveUsers = () -> {
    try {
      while (true) {
        Thread.sleep(8000);
        synchronized (this.online) {

          List<User> inactive = new ArrayList<>();

          for (User user : this.online) {
            if (System.currentTimeMillis() - user.getTimestamp() > (30 * 1000))
              inactive.add(user);
          }

          for (User user : inactive) {
            this.online.remove(user);
            FAIL.log("This should not happen " + user);
          }
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  };

  public void start() {

    LINE.log("Broadcaster Running at Port 8084");

    Set<Thread> SERVICES = Set.of(
        new Thread(sendRadarMessage),
        new Thread(receiveRadarMessages),
        new Thread(removeInactiveUsers));

    for (Thread thread : SERVICES)
      thread.start();
  }

}
