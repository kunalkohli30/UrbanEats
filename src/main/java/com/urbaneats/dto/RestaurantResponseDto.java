package com.urbaneats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {

    private Long id;

    private String name;
    private String email;
    private String description;
    private String locality;
    private String areaName;
    private String city;
    private String costForTwo;
    private String avgRatingString;
    private String totalRatingsString;
    private boolean isOpen;
    private String discountInfo;
    private String imageId;
    private List<String> cuisines;

    private String openingHours;
    private LocalDateTime registrationDate;
    private String instagram;
}
