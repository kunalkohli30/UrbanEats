package com.urbaneats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payments {

    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String userId;

    private String razorpayPaymentId;  // Razorpay Payment ID
    private String razorpayOrderId; // Razorpay Order ID
    private String paymentSignature; // Signature from Razorpay (optional)
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    private String paymentMethod; // CARD, UPI, etc.
    private String cardNetwork; // VISA, MASTERCARD, RUPAY etc.
    private String cardLast4;
    private Double amountPaid;
    private LocalDateTime paymentTime;
}
