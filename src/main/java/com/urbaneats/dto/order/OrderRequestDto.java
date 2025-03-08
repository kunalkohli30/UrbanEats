package com.urbaneats.dto.order;

import com.urbaneats.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private List<OrderItemRequestDto> orderItems;
    private String customerId;
    private long restaurantId;
    private LocalDateTime createdAt;
    private int totalItems;
//    private int totalPrice;
    private int deliveryFee;
    private int deliveryTip;
    private int gstAndCharges;
    private String couponCode;
    private Integer discountAmount;
    private OrderStatus orderStatus;
    private Long addressId;

}
