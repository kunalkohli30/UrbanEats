package com.urbaneats.dto;

import com.urbaneats.model.Category;
import com.urbaneats.model.IngredientsItem;
import com.urbaneats.model.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodRequestDto {

    private String name;
    private String description;
    private Long price;
    private boolean available;
    private boolean isVegetarian;
    private boolean isSeasonal;
    private LocalDate creationDate;
    private List<String> images;
    private List<IngredientsItem> ingredients;
    private Long restaurantId;
    private Category foodCategory;
}
