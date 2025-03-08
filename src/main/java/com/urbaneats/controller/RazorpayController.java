package com.urbaneats.controller;

import com.razorpay.RazorpayException;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.order.PaymentVerificationDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.RazorpayService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/razorpay")
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam double amount) {
        try {
            String order = String.valueOf(razorpayService.createOrder(amount));
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/payment-confirmation")
    public ResponseEntity<?> paymentConfirmation(@RequestBody PaymentVerificationDto paymentVerificationDto, HttpServletRequest request) {
        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> razorpayService.verifyPayment(paymentVerificationDto, userId))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred in payment confirmation for orderId: {}, error: {}, stacktrace: {}",
                        paymentVerificationDto.getOrderId(), throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage()))
                .flatMap(objects -> objects)
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);
    }

}
