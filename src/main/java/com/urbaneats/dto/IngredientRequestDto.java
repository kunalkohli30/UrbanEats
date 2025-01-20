package com.urbaneats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientRequestDto {

    private String ingredientName;
    private String ingredientCategoryName;
}
