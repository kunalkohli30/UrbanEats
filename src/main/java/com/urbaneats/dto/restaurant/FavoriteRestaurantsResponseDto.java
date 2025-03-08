package com.urbaneats.dto.restaurant;

import com.urbaneats.model.FavoriteRestaurants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRestaurantsResponseDto {

    private Long id;
    private Long restaurantId;

    private String name;
    private String locality;
    private String areaName;
    private String city;
    private String costForTwo;
    private String avgRatingString;
    private boolean isOpen;
    private String discountInfo;
    private String imageId;
    private List<String> cuisines;

    private LocalDateTime addedToFavoritesTimestamp;
    private String notes;

}
