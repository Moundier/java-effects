package com.example;

import java.net.*;
import java.util.*;

import com.example.User.Status;

public class ChatClient {

    private static final int CHAT_PORT = 8085;
    private static final int RADAR_PORT = 8084;
    private static final String BROADCAST_IP = "255.255.255.255";

    private User user;
    private List<User> usersOnline;
    private Map<String, Long> lastRadarMessageTime;

    public ChatClient(User user) {
        this.user = user;
        this.usersOnline = new ArrayList<>();
        this.lastRadarMessageTime = new HashMap<>();
    }

    public void sendRadarMessage() {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
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
                System.out.println("[DATA_PACKET]: " + radarMessage);

                User user = JsonUser.deserializeUser(radarMessage);
                processRadarMessage(user); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRadarMessage(User user) {
        String monitor = String.format("[MONITOR]: Any User? %s , Any Radar? %s", usersOnline, lastRadarMessageTime);
        System.out.println(monitor);
        // Check if the sender is not the current user
        if (!user.getInetAddress().equals(getLocalIpAddress())) {
            usersOnline.add(user);
            lastRadarMessageTime.put(user.getInetAddress(), System.currentTimeMillis());
        }
    }

    private void removeInactiveUsers() {
        long currentTime = System.currentTimeMillis();
        List<String> usersToRemove = new ArrayList<>();

        // Associate a time entry for a user
        for (Map.Entry<String, Long> entry : lastRadarMessageTime.entrySet()) {
            if (currentTime - entry.getValue() > 30000) { // 30 seconds in milliseconds
                usersToRemove.add(entry.getKey());
            }
        }

        for (String user : usersToRemove) {
            // Remove the user from the onlineUsers list
            lastRadarMessageTime.remove(user);
            // Handle any other cleanup or notification as needed
            
            // onlineUsers.remove();
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

        new Thread(client::sendRadarMessage).start();
        new Thread(client::receiveRadarMessages).start();

        // Add a thread to periodically remove inactive users
        new Thread(client::removeInactiveUsers).start();
    }
}
