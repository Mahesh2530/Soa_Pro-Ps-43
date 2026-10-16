package com.klu.service;

import java.time.Instant;

public class RegistrationChallenge {

    private final String phoneNumber;
    private final Instant expiresAt;
    private boolean verified;

    public RegistrationChallenge(
            String phoneNumber,
            Instant expiresAt) {

        this.phoneNumber = phoneNumber;
        this.expiresAt = expiresAt;
        this.verified = false;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}