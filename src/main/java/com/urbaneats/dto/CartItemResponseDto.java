package com.urbaneats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {

    private Long id;
    private Long cartId;
    private Long foodId;
    private String foodItemName;
    private Long restaurantId;
    private Integer quantity;
    private Integer totalPrice;
    private String restaurantName;
    private boolean isVeg;

}
