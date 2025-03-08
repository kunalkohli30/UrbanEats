package com.urbaneats.service;
import com.razorpay.*;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.order.PaymentVerificationDto;
import com.urbaneats.enums.OrderStatus;
import com.urbaneats.model.Payments;
import com.urbaneats.repository.OrderRepository;
import com.urbaneats.repository.PaymentRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String KEY_ID;

    @Value("${razorpay.key.secret}")
    private String KEY_SECRET;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public RazorpayService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(double amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Razorpay expects amount in paise (INR * 100)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", generateReceiptId());

        return razorpay.orders.create(orderRequest);
    }

    public String generateReceiptId() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder receiptId = new StringBuilder("receipt_");
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            receiptId.append(characters.charAt(random.nextInt(characters.length())));
        }
        return receiptId.toString();
    }

    public Either<Error, Map<String, String>> verifyPayment(@RequestBody PaymentVerificationDto paymentVerificationDto, String userId) {

        // Step 1: Verify Signature
        boolean isValid=false;
        try {
            isValid = Utils.verifyPaymentSignature(
                    new JSONObject(Map.of("razorpay_order_id", paymentVerificationDto.getOrderId(),
                            "razorpay_payment_id", paymentVerificationDto.getPaymentId(),
                            "razorpay_signature", paymentVerificationDto.getPaymentSignature())),
                    KEY_SECRET
            );
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        if (!isValid)
            return Either.left(new Error(ErrorType.VALIDATION_FAILED, "Invalid payment Signature"));

//        Fetch order with for provided razorpay payment order id
        Optional<com.urbaneats.model.Order> pendingOrder = getOrderByRazorpayOrderId(paymentVerificationDto.getOrderId());
        if (pendingOrder.isEmpty())
            return Either.left(new Error(ErrorType.VALIDATION_FAILED, "Order not found"));

//        Save payment details in db
        Try<Either<String, Map<String, Object>>> paymentResponse = Try.of(() -> fetchPaymentDetails(paymentVerificationDto.getPaymentId()));
        if(paymentResponse.isFailure() || paymentResponse.get().isLeft()) return Either.left(new Error(ErrorType.RAZORPAY_ERROR, "FAILED_TO_GET_PAYMENT_DETAILS"));

        Map<String, Object> paymentDetails = paymentResponse.get().get();
        LocalDateTime paymentDate = LocalDateTime.ofInstant(Instant.ofEpochSecond((Integer) paymentDetails.get("created_at")), ZoneId.systemDefault());
        String network="";
        String last4="";
        if(paymentDetails.get("method").toString().equalsIgnoreCase("card")) {
            LinkedHashMap card = (LinkedHashMap) paymentDetails.get("card");
            last4 = card.get("last4").toString();
            network = card.get("network").toString();
        }
        Payments payment = Payments.builder()
                .order(pendingOrder.get())
                .userId(userId)
                .razorpayPaymentId(paymentVerificationDto.getPaymentId())
                .razorpayOrderId(paymentVerificationDto.getOrderId())
                .paymentSignature(paymentVerificationDto.getPaymentSignature())
                .paymentStatus("SUCCESS")
                .amountPaid(pendingOrder.get().getTotalAmount().doubleValue())
                .paymentTime(paymentDate)
                .paymentMethod((String) paymentDetails.getOrDefault("method", "card"))
                .cardNetwork(network)
                .cardLast4(last4)
                .build();

        Payments saved = paymentRepository.save(payment);

//        Update order table

        pendingOrder.get().setOrderStatus(OrderStatus.CONFIRMED.name());
        orderRepository.save(pendingOrder.get());

        return Either.right(Collections.singletonMap("message", "Payment verified & order confirmed!"));
    }

    private Optional<com.urbaneats.model.Order> getOrderByRazorpayOrderId(String orderId) {
        return  orderRepository.findByRazorpayOrderId(orderId);
    }

    public Either<String, Map<String, Object>> fetchPaymentDetails(String paymentId) {
        String url = "https://api.razorpay.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(KEY_ID, KEY_SECRET); // Razorpay API Authentication
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if(response.getStatusCode().isSameCodeAs(HttpStatus.OK))
            return Either.right(response.getBody()); // Returns payment details as a JSON Map
        else
            return Either.left("INVALID_PAYMENT_ID");
    }

    public String generateSignature(String orderId, String paymentId) throws Exception {
        String data = orderId + "|" + paymentId; // Step 1: Concatenate order_id & payment_id

        // Step 2: Generate HMAC-SHA256 Hash
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(KEY_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes());

        // Step 3: Convert Hash to Base64 String
        return Base64.getEncoder().encodeToString(hash);
    }

    public Payments findPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
