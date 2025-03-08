package com.urbaneats.dto.restaurant;

import com.urbaneats.dto.discountInfoTemporary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDataInsertDto {

    private String name;
    private String locality;
    private String areaName;
    private String costForTwo;
    private List<String> cuisines;
    private String avgRatingString;
    private String totalRatingsString;
    private boolean isOpen;
    private discountInfoTemporary discountInfo;
    private String email;
    private String imageId;


}
