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
    private Set<User> online = new HashSet<>();

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
        } 
        catch (Exception e) {
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
                DONE.log("Counter: " + computed());
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String radarMessage = new String(packet.getData(), 0, packet.getLength());
                INFO.log("Found: " + radarMessage);
                User receivedUser = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(receivedUser);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    };

    void processRadarMessage(User receivedUser) {

        synchronized (online) {

            Boolean isFound = (online.isEmpty());
            Boolean isCurrent = (receivedUser.getInetAddress().equals(Host.fetchLocalIP()));

            if (isFound) WARN.log("No users online.");

            if (!isFound) INFO.log("Online " + online);

            if (!isCurrent) {
                online.add(receivedUser);
                online.notify();
            } 
            /*
            else {
                online.add(receivedUser);
                online.notify();
            }  */
        }
    }

    Runnable removeInactiveUsers = () -> {
        try {
            while (true) {
                Thread.sleep(8000);
                synchronized (online) {
                    
                    List<User> inactive = new ArrayList<>();
                    boolean userTimeoutCondition = System.currentTimeMillis() - user.getTimestamp() > 30000; 

                    for (User user : online) 
                        if (userTimeoutCondition)
                            inactive.add(user);

                    for (User user : inactive) {
                        online.remove(user);
                        WARN.log("This should not happen " + user);
                    }
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
            new Thread(removeInactiveUsers)
        );

        for (Thread thread : SERVICES) thread.start();
    }

}
