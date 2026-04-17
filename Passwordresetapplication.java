package com.passwordreset.controller;

import com.passwordreset.exception.PasswordResetException;
import com.passwordreset.model.OTPRequest;
import com.passwordreset.model.ResetRequest;
import com.passwordreset.service.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
@Slf4j
@CrossOrigin(origins = "*")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOTP(@RequestBody OTPRequest otpRequest) {
        log.info("Received request to generate OTP for email: {}", otpRequest.getEmail());

        try {
            passwordResetService.generateAndSendOTP(otpRequest.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP sent successfully to your email");
            response.put("email", otpRequest.getEmail());
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (PasswordResetException e) {
            log.error("Error generating OTP: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest resetRequest) {
        log.info("Received password reset request for email: {}", resetRequest.getEmail());

        try {
            passwordResetService.resetPassword(resetRequest);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (PasswordResetException e) {
            log.error("Error resetting password: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        log.info("Checking if email exists: {}", email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("exists", passwordResetService.emailExists(email));

        return ResponseEntity.ok(response);
    }
}
