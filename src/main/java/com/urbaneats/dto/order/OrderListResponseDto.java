package com.urbaneats.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class OrderListResponseDto {

    private Long orderId;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantLocality;
    private String restaurantImageUrl;
    private String orderStatus;
//    private String deliveryStatus;
    private int totalItem;
    private Integer totalAmount;
    private LocalDateTime createdAt;
    private Integer deliveryTimeInSeconds;
    private List<OrderItemResponseDto> orderItems;
    private String userId;
    private Integer deliveryFee;
    private Integer deliveryTip;
    private Integer gstAndFees;
    private Integer discountAmount;
    private String deliveryAddress;
    private String addressName;
    private String paymentMethod;
    private String cardNetwork;
    private String cardLast4;

}
