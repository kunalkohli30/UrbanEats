package com.urbaneats.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.sender}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOTPEmail(String recipientEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("UrbanEatsDelivery <" + senderEmail + ">");
            helper.setTo(recipientEmail);
            helper.setSubject("Your OTP Code for Food Delivery 🍔");

            // Load the HTML template and replace {{OTP_CODE}} with actual OTP
            String emailContent = getEmailTemplate(otp);

            helper.setText(emailContent, true); // true = HTML content
            mailSender.send(message);

            System.out.println("OTP email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            throw new RuntimeException("Error while sending OTP email", e);
        }
    }

    private String getEmailTemplate(String otp) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Your OTP Code</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #fff8f0;
                            margin: 0;
                            padding: 0;
                        }
                        .email-container {
                            max-width: 500px;
                            margin: 20px auto;
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                            text-align: center;
                            border-top: 5px solid #ff7b00;
                        }
                        .header img {
                            width: 100%;
                            border-radius: 10px 10px 0 0;
                        }
                        .otp-code {
                            font-size: 28px;
                            font-weight: bold;
                            color: #ff7b00;
                            background: #fff3e0;
                            padding: 10px 20px;
                            border-radius: 5px;
                            display: inline-block;
                            margin-top: 15px;
                            letter-spacing: 3px;
                        }
                        .footer {
                            font-size: 12px;
                            color: #888;
                            margin-top: 20px;
                        }
                        .cta-button {
                            background: #ff7b00;
                            color: white;
                            text-decoration: none;
                            padding: 12px 20px;
                            border-radius: 5px;
                            font-size: 16px;
                            display: inline-block;
                            margin-top: 20px;
                        }
                        .cta-button:hover {
                            background: #e66a00;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="header">
                            <img src="https://source.unsplash.com/500x200/?food,delicious" alt="Delicious Food">
                        </div>
                        <h2>Your OTP Code 🍔</h2>
                        <p>Use the following One-Time Password (OTP) to verify your identity and complete your login or order. This OTP is valid for <b>5 minutes</b>.</p>
                        <div class="otp-code">""" + otp + """
                        </div>
                        <p>If you didn’t request this, you can ignore this email.</p>
                        <a href="#" class="cta-button">Go to App</a>
                        <p class="footer">Bon Appétit! 🍕 <br> <b>Your Food Delivery Team</b></p>
                    </div>
                </body>
                </html>
                """;
    }
}
