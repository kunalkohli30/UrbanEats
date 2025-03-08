package com.urbaneats.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long orderItemId;
    private Long foodId;
    private String foodItemName;
    private Integer foodItemPrice;
    private boolean isFoodItemVeg;
    private Integer quantity;
    private Integer totalPrice;

}
