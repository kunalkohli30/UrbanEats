package com.urbaneats.controller;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.OTPService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/otp")
@Slf4j
public class OTPController {

    private final OTPService otpService;

    public OTPController(OTPService otpService) {
        this.otpService = otpService;
    }

    // Generate OTP
    @PostMapping("/generate")
    public String generateOTP(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {

//        String userId = httpServletRequest.getAttribute("uid").toString();
        String email = request.get("email"); // Get email from request body
        return otpService.generateAndSendOTP(email);
    }

    // Validate OTP
    @PostMapping("/validate")
    public ResponseEntity<?> validateOTP(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {

//        String userId = httpServletRequest.getAttribute("uid").toString();

        String email = request.get("email");
        String otp = request.get("otp");
        return Try.of(() -> otpService.validateOTP(email, otp))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while validating OTP. {}", throwable.getMessage()))
                .mapLeft(throwable -> new Error(ErrorType.VALIDATION_FAILED, "Error occurred while validating OTP"))
                .fold(ErrorResponseHandler::respondError,
                        response -> ResponseEntity.ok(response ? "OTP_VALIDATION_SUCCESS" : "OTP_VALIDATION_FAILED" ));
    }
}
