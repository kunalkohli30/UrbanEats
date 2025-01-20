package com.urbaneats.dto.dataSetup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDataEntry {

    private String name;
    private String description;
    private String imageId;
    private String category;
    private Integer price;
    private boolean vegetarian;
    private Long restaurantId;

}
