package com.example.service;

import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.example.model.User;
import com.example.model.User.Status;

import com.example.utils.JsonUser;
import com.example.utils.Host;
import com.example.utils.Console.DONE;
import com.example.utils.Console.INFO;
import com.example.utils.Console.LINE;
import com.example.utils.Console.WARN;

public class Broadcaster {

    private final int RADAR_PORT = 8084;
    private final String BROADCAST_IP = "255.255.255.255";

    private User user;
    private Set<User> usersOnline;
    private Map<User, Long> lastRadarMessageTime;

    public Broadcaster(User user) {
        this.user = user;
        this.usersOnline = new HashSet<>();
        this.lastRadarMessageTime = new HashMap<>();
        initProbe(this.user.getUsername());
    }

    Runnable sendRadarMessage = () -> {
        
        try {
          synchronized (this) {
            if (user == null) this.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                DONE.log("We are sending!");
                String radarMessage = JsonUser.serializeUser(user);
                byte[] sendData = radarMessage.getBytes();
                InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP);
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress, RADAR_PORT);
                socket.send(packet);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private int MONITOR_CYCLE = 0;

    public int updateCycle() {
        return MONITOR_CYCLE = MONITOR_CYCLE + 1;
    }

    Runnable receiveRadarMessages = () -> {
        try (DatagramSocket socket = new DatagramSocket(RADAR_PORT)) {
            while (true) {
                DONE.log("cycle " + updateCycle());
                byte[] receiveData = new byte[4096];
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(packet);
                String radarMessage = new String(packet.getData(), 0, packet.getLength());
                DONE.log("received: " + radarMessage);
                User receivedUser = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(receivedUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    void processRadarMessage(User receivedUser) {

        if (usersOnline.isEmpty()) {
            WARN.log("No users online.");
        } else {
            INFO.log(String.format("monitor: Found online users: %s", usersOnline));
        }

        if (!receivedUser.getInetAddress().equals(Host.fetchLocalIP())) {
            
            // usersOnline.add(receivedUser);
            
            synchronized(usersOnline) {
                usersOnline.add(receivedUser);
                usersOnline.notify();
            }
            lastRadarMessageTime.put(receivedUser, System.currentTimeMillis());
        }
    }

    Runnable removeInactiveUsers = () -> {
        try {
            while (true) {
                long currentTime = System.currentTimeMillis();

                LINE.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                DONE.log("...Epoch Interval(ms): " + currentTime);
                Set<User> usersToRemove = new HashSet<>();

                for (User user : lastRadarMessageTime.keySet()) {
                    long lastActiveTime = lastRadarMessageTime.get(user);
                    if (currentTime - lastActiveTime > 30000) {
                        usersToRemove.add(user);
                    }
                }

                for (User user : usersToRemove) {
                    lastRadarMessageTime.remove(user);
                    synchronized(usersOnline) {
                        usersOnline.remove(user);
                        usersOnline.notify();
                    }
                }

                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public void initProbe(String username) {

        INFO.log(String.format("building the user %s...", username));

        INFO.log("threading up...");
        List<Thread> SERVICES = List.of(
                new Thread(sendRadarMessage),
                new Thread(receiveRadarMessages),
                new Thread(removeInactiveUsers));

        INFO.log("threads backgrounding...");
        for (Thread thread : SERVICES)
            thread.start();
    }

    // Keep for testing

    // public static void main(String[] args) throws InterruptedException{
    // User user = User.builder()
    // .username("Casanova")
    // .inetAddress(Host.fetchLocalIP())
    // .timestamp(System.currentTimeMillis())
    // .status(Status.ONLINE)
    // .build();

    // Broadcaster broadcaster = new Broadcaster(user); // Pass an initial User
    // object or null
    // broadcaster.initProbe("Casanova");
    // }
}
