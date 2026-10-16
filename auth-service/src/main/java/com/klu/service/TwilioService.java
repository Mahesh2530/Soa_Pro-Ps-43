package com.klu.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final String accountSid;
    private final String authToken;
    private final String verifyServiceSid;

    public TwilioService(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.verify-service-sid}") String verifyServiceSid) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.verifyServiceSid = verifyServiceSid;

        Twilio.init(accountSid, authToken);
    }

    // Convert Indian mobile number to E.164 format
    private String normalizePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new RuntimeException("Mobile number is required");
        }

        phoneNumber = phoneNumber.trim();

        // Already in +91 format
        if (phoneNumber.startsWith("+91")) {
            return phoneNumber;
        }

        // 91XXXXXXXXXX
        if (phoneNumber.startsWith("91")
                && phoneNumber.length() == 12) {
            return "+" + phoneNumber;
        }

        // 10-digit Indian mobile number
        if (phoneNumber.matches("[6-9][0-9]{9}")) {
            return "+91" + phoneNumber;
        }

        throw new RuntimeException(
                "Invalid Indian mobile number"
        );
    }

    public String sendOtp(String phoneNumber) {

        String normalizedNumber =
                normalizePhoneNumber(phoneNumber);

        Verification verification =
                Verification.creator(
                        verifyServiceSid,
                        normalizedNumber,
                        "sms"
                ).create();

        return verification.getStatus();
    }

    public String verifyOtp(
            String phoneNumber,
            String code) {

        String normalizedNumber =
                normalizePhoneNumber(phoneNumber);

        VerificationCheck verificationCheck =
                VerificationCheck.creator(
                        verifyServiceSid
                )
                .setTo(normalizedNumber)
                .setCode(code)
                .create();

        return verificationCheck.getStatus();
    }
}