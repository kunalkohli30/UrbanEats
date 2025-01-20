package com.urbaneats.dto;

import com.urbaneats.model.Category;
import com.urbaneats.model.IngredientsItem;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodItemsResponseDto {

    private Long id;
    private String name;
    private String description;
    private Integer price;
    private boolean available;
    private boolean isVegetarian;
    private boolean isSeasonal;
    private LocalDate creationDate;
    private String imageId;
    private Long restaurantId;
    private String foodCategory;
}
