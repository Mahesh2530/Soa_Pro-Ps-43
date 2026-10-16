package com.klu.controller;

public class LoginOtpResponse {

    private String message;
    private String challengeId;
    private String phoneNumber;

    public LoginOtpResponse(String message, String challengeId, String phoneNumber) {
        this.message = message;
        this.challengeId = challengeId;
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}