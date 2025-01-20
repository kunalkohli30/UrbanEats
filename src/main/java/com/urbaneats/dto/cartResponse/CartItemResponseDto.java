package com.urbaneats.dto.cartResponse;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonRootName("cartItem")
public class CartItemResponseDto {

    private Long foodId;
    private String foodItemName;
    private boolean isVeg;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
}
