package com.example.service;

import java.net.*;
import java.util.*;

import com.example.model.User;
import com.example.model.User.Status;
import com.example.utils.JsonUser;
import com.example.utils.Host;

public class ProbeService {

    // Constants
    private static final int CHAT_PORT = 8085;
    private static final int RADAR_PORT = 8084;
    private static final String BROADCAST_IP = "255.255.255.255";

    // Attributes
    protected User user;
    protected Set<User> usersOnline;
    protected Map<User, Long> lastRadarMessageTime;

    // Constructor
    public ProbeService(User user) {
        this.user = user;
        this.usersOnline = new HashSet<>();
        this.lastRadarMessageTime = new HashMap<>();
    }

    public void sendRadarMessage() {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                System.out.println("--------------------------------------------------------------");
                System.out.println("[Socket_START]: Sending to Socket");
                String radarMessage = JsonUser.serializeUser(this.user);
                byte[] sendData = radarMessage.getBytes();
                InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP); // We send in BROAD_CAST
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress, RADAR_PORT);
                socket.send(packet);
                Thread.sleep(5000); // Envie a cada 5 segundos
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int counter = 0; 

    public void receiveRadarMessages() {
        try (DatagramSocket socket = new DatagramSocket(RADAR_PORT)) {
            while (true) {
                counter = counter + 1;
                System.out.println("[Socket_READ]: cycle " + counter);
                byte[] receiveData = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(packet);
                String radarMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("[Sent_DATA_PACKET]: " + radarMessage);

                User user = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(user); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRadarMessage(User user) {
        String monitor = String.format("[Watch_MONITOR]: Any User? %s", usersOnline);
        System.out.println(monitor);
        // Check if the sender is not the current user
        if (!user.getInetAddress().equals(Host.getIpAddress())) {
            // Here, Dont need to add to a list, but to the interface list instead
            usersOnline.add(user);
            lastRadarMessageTime.put(user, System.currentTimeMillis());
        }
    }

    private void removeInactiveUsers() {
        try {
            while (true) {
                long currentTime = System.currentTimeMillis();
                System.out.println("Current Time in Milliseconds: " + currentTime + "ms");
    
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
    }

    public static void initProbe(String username) {

        User user = User.builder()
        .username(username)
        .inetAddress(Host.getIpAddress())
        .timestamp(System.currentTimeMillis())
        .status(Status.ONLINE)
        .build();
        
        ProbeService probeService = new ProbeService(user);

        List<Thread> thread_list = List.of(
            new Thread(probeService::sendRadarMessage),
            new Thread(probeService::receiveRadarMessages),
            new Thread(probeService::removeInactiveUsers)
        );

        for (Thread thread : thread_list) {
            thread.start();
        }
    }

    public static void main(String[] args) {
        
        User user = User.builder()
        .username("Casanova")
        .inetAddress(Host.getIpAddress())
        .timestamp(System.currentTimeMillis())
        .status(Status.ONLINE)
        .build();
        
        ProbeService probeService = new ProbeService(user);

        List<Thread> thread_list = List.of(
            new Thread(probeService::sendRadarMessage),
            new Thread(probeService::receiveRadarMessages),
            new Thread(probeService::removeInactiveUsers)
        );

        for (Thread thread : thread_list) {
            thread.start();
        }
    }

}
