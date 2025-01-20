package com.urbaneats.dto.cartResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {

    private Long cartId;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;
    private String restaurantAreaName;

    private List<CartItemResponseDto> cartItems;
}
