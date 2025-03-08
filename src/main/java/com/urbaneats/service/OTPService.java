package com.urbaneats.service;

import com.urbaneats.model.OTP;
import com.urbaneats.repository.OTPRepository;
import com.urbaneats.utility.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    private final OTPRepository otpRepository;
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    @Autowired
    public OTPService(OTPRepository otpRepository, JavaMailSender mailSender, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
        this.emailService = emailService;
    }

    // Generate and send OTP
    public String generateAndSendOTP(String emailId) {
        String otp = OTPUtil.generateOTP(6); // Generate 6-digit OTP
        otpRepository.findByEmail(emailId)
                .ifPresent(otpEntity -> otpRepository.deleteById(otpEntity.getId()));

        // Store OTP with expiration time
        OTP otpEntity = OTP.builder()
                .email(emailId)
//                .userId(userId)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5)) // OTP valid for 5 minutes
                .build();
        otpRepository.save(otpEntity);

        // Send OTP (via email or SMS)
        System.out.println("OTP sent to " + emailId + ": " + otp);
        emailService.sendOTPEmail(emailId, otp);
        return "OTP sent successfully!";
    }

    // Validate OTP
    public boolean validateOTP(String email, String otp) {
        return otpRepository.findByEmail(email)
                .map(otpEntity -> otpEntity.getOtp().equals(otp) && LocalDateTime.now().isBefore(otpEntity.getExpiryTime())) // OTP is valid
                .orElse(false);     // OTP not found
    }

    private void sendEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP is: " + otp);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}

