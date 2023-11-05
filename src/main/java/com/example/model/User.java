package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.InetAddress; // Import InetAddress

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    /* Next fix attributes */
    @EqualsAndHashCode.Include
    private String username;

    @EqualsAndHashCode.Exclude
    private Long timestamp;

    @EqualsAndHashCode.Exclude
    private Status status;

    @EqualsAndHashCode.Include
    private InetAddress inetAddress; // Use InetAddress instead of String

    public enum Status { 
        ONLINE,
        BE_BACK_SOON, 
        DO_NOT_DISTURB, 
    }

    public void updateTimestap() {
        this.timestamp = System.currentTimeMillis();
    }

    public void updateStatus(User.Status status) {
        this.status = status;
    }
}