package com.klu.controller;

import com.klu.entity.User;
import com.klu.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================================================
    // REGISTER - STEP 1
    // SEND OTP
    // =========================================================

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(
            @RequestBody RegisterOtpRequest request) {

        try {

            authService.sendRegistrationOtp(
                    request.getPhoneNumber()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message", "OTP sent successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // REGISTER - STEP 2
    // VERIFY OTP
    // =========================================================

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyRegistrationOtp(
            @RequestBody VerifyRegisterOtpRequest request) {

        try {

            authService.verifyRegistrationOtp(
                    request.getPhoneNumber(),
                    request.getCode()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "verified", true,
                            "message", "Mobile number verified"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // REGISTER - STEP 3
    // CREATE ACCOUNT
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {

        try {

            User savedUser =
                    authService.register(user);

            // Never return password
            savedUser.setPassword(null);

            return ResponseEntity.ok(savedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // LOGIN
    // MOBILE + PASSWORD ONLY
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            String token =
                    authService.login(
                            request.getPhoneNumber(),
                            request.getPassword()
                    );

            return ResponseEntity.ok(
                    new LoginResponse(token)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 1
    // SEND OTP
    // =========================================================

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordOtpRequest request) {

        try {

            authService.sendForgotPasswordOtp(
                    request.getPhoneNumber()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message", "OTP sent successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 2
    // VERIFY OTP
    // =========================================================

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @RequestBody VerifyForgotPasswordOtpRequest request) {

        try {

            authService.verifyForgotPasswordOtp(
                    request.getPhoneNumber(),
                    request.getCode()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "verified", true,
                            "message", "Mobile number verified"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 3
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {

            authService.resetPassword(
                    request.getPhoneNumber(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Password reset successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}