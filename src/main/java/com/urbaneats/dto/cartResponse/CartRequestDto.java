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
public class CartRequestDto {

    private Long restaurantId;
    private List<CartItemSubDtoForSaveCart> cartItems;
}
