package com.klu.service;

import java.time.Instant;

public class LoginChallenge {

    private String phoneNumber;
    private Instant expiresAt;

    public LoginChallenge(String phoneNumber, Instant expiresAt) {
        this.phoneNumber = phoneNumber;
        this.expiresAt = expiresAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}