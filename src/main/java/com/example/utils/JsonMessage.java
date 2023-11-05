package com.example.utils;

import com.example.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessage {
    
    public static String serializeMessage(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Message deserializeMessage(String fromJSON) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(fromJSON, Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
