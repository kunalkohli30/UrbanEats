package com.urbaneats.dto.cartResponse;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemSubDtoForSaveCart {

    private Long foodId;
    private Integer quantity;
}
