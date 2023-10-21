package com.example.service;

import java.net.*;
import java.util.*;

import com.example.model.User;
import com.example.model.User.Status;

import com.example.utils.JsonUser;
import com.example.utils.Host;
import com.example.utils.Console.DONE;
import com.example.utils.Console.INFO;
import com.example.utils.Console.LINE;

public class Broadcaster {

    // Constants
    // private static final int CHAT_PORT = 8085;
    private static final int RADAR_PORT = 8084;
    private static final String BROADCAST_IP = "255.255.255.255";

    // Attributes
    protected static User user;
    protected static Set<User> usersOnline;
    protected static Map<User, Long> lastRadarMessageTime;

    // Constructor
    public Broadcaster(User user) {
        Broadcaster.user = user;
        Broadcaster.usersOnline = new HashSet<>();
        Broadcaster.lastRadarMessageTime = new HashMap<>();
    }

    static Runnable sendRadarMessage = () -> {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                String radarMessage = JsonUser.serializeUser(user);
                byte[] sendData = radarMessage.getBytes();
                InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP); // We send in BROAD_CAST
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress, RADAR_PORT);
                socket.send(packet);
                Thread.sleep(5000); // Envie a cada 5 segundos
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private static int MONITOR_CYCLE = 0;

    public static int updateCycle () {
        return MONITOR_CYCLE = MONITOR_CYCLE + 1;
    }

    static Runnable receiveRadarMessages = () -> {
        try (DatagramSocket socket = new DatagramSocket(RADAR_PORT)) {
            while (true) {
                DONE.log("cycle " + updateCycle());
                byte[] receiveData = new byte[4096];
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(packet);
                String radarMessage = new String(packet.getData(), 0, packet.getLength());
                DONE.log("received: " + radarMessage);
                User user = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    static void processRadarMessage(User user) {

        INFO.log(String.format("monitor: Found %s", usersOnline));
        // Check if the sender is not the current user
        if (!user.getInetAddress().equals(Host.getIpAddress())) {
            usersOnline.add(user);
            lastRadarMessageTime.put(user, System.currentTimeMillis());
        }
    }

    static Runnable removeInactiveUsers = () -> {
        try {
            while (true) {
                long currentTime = System.currentTimeMillis();
                
                LINE.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                DONE.log("...Epoch Interval(ms): " + currentTime);
                Set<User> usersToRemove = new HashSet<>();

                for (User user : lastRadarMessageTime.keySet()) {
                    long lastActiveTime = lastRadarMessageTime.get(user);
                    if (currentTime - lastActiveTime > 30000) { // Change to milliseconds
                        usersToRemove.add(user);
                    }
                }

                for (User user : usersToRemove) {
                    lastRadarMessageTime.remove(user);
                    usersOnline.remove(user);
                }

                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public static void initProbe(String username) {

        INFO.log(String.format("building the user %s...", username));
        new Broadcaster(User.builder()
                .username(username)
                .inetAddress(Host.getIpAddress())
                .timestamp(System.currentTimeMillis())
                .status(Status.ONLINE)
                .build());

        INFO.log("threading up...");
        List<Thread> thread_list = List.of(
                new Thread(sendRadarMessage),
                new Thread(receiveRadarMessages),
                new Thread(removeInactiveUsers));

        INFO.log("threads backgrounding...");
        for (Thread thread : thread_list) thread.start();
    }

    public static void main(String[] args) {
        // Keep for Debugging
        initProbe("Casanova");
    }
}
