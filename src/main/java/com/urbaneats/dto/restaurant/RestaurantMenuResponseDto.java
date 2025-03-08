package com.urbaneats.dto.restaurant;

import com.urbaneats.dto.FoodItemsResponseDto;
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
