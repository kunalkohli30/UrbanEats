package com.urbaneats.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentVerificationDto {

    private String orderId;         // razorpayOrderId
    private String paymentId;
    private String paymentSignature;
}
