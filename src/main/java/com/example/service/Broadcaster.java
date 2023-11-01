package com.example.service;

import java.net.*;
import java.util.*;

import com.example.model.User;
import com.example.utils.JsonUser;
import com.example.utils.Host;
import com.example.utils.Console.DONE;
import com.example.utils.Console.INFO;
import com.example.utils.Console.LINE;
import com.example.utils.Console.WARN;

import lombok.Data;

@Data
public class Broadcaster {

    private final int RADAR_PORT = 8084;
    private final String BROADCAST_IP = "255.255.255.255";

    private User user;
    private Set<User> usersOnline = new HashSet<>();

    public Broadcaster(User user) {
        this.user = user;
        initProbe(this.user.getUsername());
    }

    Runnable sendRadarMessage = () -> {
        try (DatagramSocket socket = new DatagramSocket()) {

            while (true) {

                DONE.log("The Broadcaster Thread is Working...");
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

                DONE.log("Counter: " + updateCycle());
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String radarMessage = new String(packet.getData(), 0, packet.getLength());
                INFO.log("Found: " + radarMessage);
                User receivedUser = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(receivedUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    void processRadarMessage(User receivedUser) {

        synchronized (usersOnline) {

            Boolean isFound = (usersOnline.isEmpty());
            Boolean isCurrent = (receivedUser.getInetAddress().equals(Host.fetchLocalIP()));

            if (isFound)
                WARN.log("No users online.");

            if (!isFound)
                INFO.log("Online " + usersOnline);

            if (!isCurrent) {
                usersOnline.add(receivedUser);
                usersOnline.notify();
            }
        }
    }

    Runnable removeInactiveUsers = () -> {
        try {
            while (true) {
                Thread.sleep(8000);
                synchronized (usersOnline) {
                    List<User> inactive = new ArrayList<>();
                    for (User user : usersOnline) {
                        if (System.currentTimeMillis() - user.getTimestamp() > 30000) {
                            inactive.add(user);
                            WARN.log("This should not happen " + user);
                        }
                    }

                    for (User user : inactive)
                        usersOnline.remove(user);
                }
            } 
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public void initProbe(String username) {

        LINE.log("building the user " + username);
        LINE.log("Broadcaster Running at Port 8084");

        List<Thread> SERVICES = List.of(
                new Thread(sendRadarMessage),
                new Thread(receiveRadarMessages),
                new Thread(removeInactiveUsers));

        for (Thread thread : SERVICES)
            thread.start();
    }

}
