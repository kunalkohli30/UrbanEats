package com.urbaneats.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantMenuResponseDto {

    private String category;
    private List<FoodItemsResponseDto> foodItems;
}
