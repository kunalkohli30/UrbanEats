package com.urbaneats.dto.order;

import com.urbaneats.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class OrderResponseDto {

    private Long orderId;
    private String razorPayOrderId;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private List<OrderItemRequestDto> orderItems;
    private Integer deliveryTime;
    private Integer amount;
    private String receiptId;
}
