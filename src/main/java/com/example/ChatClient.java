package com.example;

import java.net.*;
import java.util.*;

import com.example.User.Status;

public class ChatClient {

    private static final int CHAT_PORT = 8085;
    private static final int RADAR_PORT = 8084;
    private static final String BROADCAST_IP = "255.255.255.255";

    private User user;
    private Set<User> usersOnline;
    private Map<User, Long> lastRadarMessageTime;

    public ChatClient(User user) {
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
        if (!user.getInetAddress().equals(getLocalIpAddress())) {
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

    private static String getLocalIpAddress() {
        try {
            // Get the local host's address and return it as a string
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "192.168.0.6"; // Default or fallback value
        }
    }

    public static void main(String[] args) {

        User user = User.builder()
        .username("Casanova")
        .status(Status.ONLINE)
        .timestamp(System.currentTimeMillis())
        .inetAddress(getLocalIpAddress())
        .build();

        ChatClient client = new ChatClient(user);

        List<Thread> thread_list = List.of(
            new Thread(client::sendRadarMessage),
            new Thread(client::receiveRadarMessages),
            new Thread(client::removeInactiveUsers)
        );

        for (Thread thread : thread_list) {
            thread.start();
        }

    }
}
