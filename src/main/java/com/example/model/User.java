package com.example.model;

import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    @EqualsAndHashCode.Exclude
    private String username;

    @EqualsAndHashCode.Exclude
    private Long timestamp;

    @EqualsAndHashCode.Exclude
    private Status status;

    @EqualsAndHashCode.Include
    private String inetAddress; // InetAddress 

    public enum Status { IDLE, AVOID, ONLINE }
}
