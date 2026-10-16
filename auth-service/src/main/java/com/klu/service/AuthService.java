package com.klu.service;

import com.klu.entity.User;
import com.klu.repository.UserRepository;
import com.klu.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TwilioService twilioService;

    // =========================================================
    // VERIFIED MOBILE NUMBERS
    // =========================================================

    private final Set<String> verifiedRegistrationPhones =
            ConcurrentHashMap.newKeySet();

    private final Set<String> verifiedResetPhones =
            ConcurrentHashMap.newKeySet();

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TwilioService twilioService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.twilioService = twilioService;
    }

    // =========================================================
    // REGISTER - STEP 1
    // SEND OTP
    // =========================================================

    public void sendRegistrationOtp(String phoneNumber) {

        if (phoneNumber == null ||
                phoneNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        phoneNumber = phoneNumber.trim();

        if (userRepository.existsByPhoneNumber(phoneNumber)) {

            throw new RuntimeException(
                    "User with this mobile number already exists"
            );
        }

        String status =
                twilioService.sendOtp(phoneNumber);

        if (!"pending".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Unable to send OTP"
            );
        }
    }

    // =========================================================
    // REGISTER - STEP 2
    // VERIFY OTP
    // =========================================================

    public void verifyRegistrationOtp(
            String phoneNumber,
            String code) {

        if (phoneNumber == null ||
                phoneNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        if (code == null ||
                code.trim().isEmpty()) {

            throw new RuntimeException(
                    "OTP is required"
            );
        }

        phoneNumber = phoneNumber.trim();
        code = code.trim();

        String status =
                twilioService.verifyOtp(
                        phoneNumber,
                        code
                );

        if (!"approved".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        // Mark this mobile number as verified
        verifiedRegistrationPhones.add(phoneNumber);
    }

    // =========================================================
    // REGISTER - STEP 3
    // CREATE ACCOUNT
    // =========================================================

    public User register(User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User data is required"
            );
        }

        if (user.getPhoneNumber() == null ||
                user.getPhoneNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        String phoneNumber =
                user.getPhoneNumber().trim();

        user.setPhoneNumber(phoneNumber);

        // Check OTP verification
        if (!verifiedRegistrationPhones.contains(phoneNumber)) {

            throw new RuntimeException(
                    "Mobile number must be verified before registration"
            );
        }

        // Check strong password
        if (!isStrongPassword(user.getPassword())) {

            throw new RuntimeException(
                    "Password must be at least 6 characters and contain " +
                    "one uppercase letter, one number, and one special character."
            );
        }

        // Check duplicate user
        if (userRepository.existsByPhoneNumber(phoneNumber)) {

            throw new RuntimeException(
                    "User with this mobile number already exists"
            );
        }

        // BCrypt password
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        user.setRole("FARMER");

        user.setCreatedAt(
                LocalDateTime.now()
        );

        User savedUser =
                userRepository.save(user);

        // Prevent OTP verification reuse
        verifiedRegistrationPhones.remove(phoneNumber);

        return savedUser;
    }

    // =========================================================
    // LOGIN
    // MOBILE + PASSWORD ONLY
    // NO OTP
    // =========================================================

    public String login(
            String phoneNumber,
            String password) {

        if (phoneNumber == null ||
                password == null) {

            throw new RuntimeException(
                    "Invalid mobile number or password"
            );
        }

        phoneNumber = phoneNumber.trim();

        User user =
                userRepository
                        .findByPhoneNumber(phoneNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid mobile number or password"
                                )
                        );

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid mobile number or password"
            );
        }

        return jwtService.generateToken(
                user.getPhoneNumber(),
                user.getRole()
        );
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 1
    // SEND OTP
    // =========================================================

    public void sendForgotPasswordOtp(
            String phoneNumber) {

        if (phoneNumber == null ||
                phoneNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        phoneNumber = phoneNumber.trim();

        // User must already exist
        if (!userRepository.existsByPhoneNumber(
                phoneNumber)) {

            throw new RuntimeException(
                    "No account found with this mobile number"
            );
        }

        String status =
                twilioService.sendOtp(phoneNumber);

        if (!"pending".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Unable to send OTP"
            );
        }
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 2
    // VERIFY OTP
    // =========================================================

    public void verifyForgotPasswordOtp(
            String phoneNumber,
            String code) {

        if (phoneNumber == null ||
                phoneNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        if (code == null ||
                code.trim().isEmpty()) {

            throw new RuntimeException(
                    "OTP is required"
            );
        }

        phoneNumber = phoneNumber.trim();
        code = code.trim();

        String status =
                twilioService.verifyOtp(
                        phoneNumber,
                        code
                );

        if (!"approved".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        // Mark mobile as verified for password reset
        verifiedResetPhones.add(phoneNumber);
    }

    // =========================================================
    // STRONG PASSWORD VALIDATION
    // =========================================================

    private boolean isStrongPassword(
            String password) {

        if (password == null ||
                password.length() < 6) {

            return false;
        }

        boolean hasUppercase =
                password.matches(".*[A-Z].*");

        boolean hasNumber =
                password.matches(".*\\d.*");

        boolean hasSpecial =
                password.matches(
                        ".*[^A-Za-z0-9].*"
                );

        return hasUppercase &&
                hasNumber &&
                hasSpecial;
    }

    // =========================================================
    // FORGOT PASSWORD - STEP 3
    // RESET PASSWORD
    // =========================================================

    public void resetPassword(
            String phoneNumber,
            String newPassword) {

        if (phoneNumber == null ||
                phoneNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        phoneNumber = phoneNumber.trim();

        // OTP must be verified first
        if (!verifiedResetPhones.contains(phoneNumber)) {

            throw new RuntimeException(
                    "Mobile number must be verified first"
            );
        }

        if (!isStrongPassword(newPassword)) {

            throw new RuntimeException(
                    "Password must be at least 6 characters and contain " +
                    "one uppercase letter, one number, and one special character."
            );
        }

        User user =
                userRepository
                        .findByPhoneNumber(phoneNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        // BCrypt hash
        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);

        // Prevent OTP verification reuse
        verifiedResetPhones.remove(phoneNumber);
    }
}