package com.urbaneats.dto.cartResponse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequestDto {

    private Long foodId;
    private String operation;
    private Long totalPrice;

}
