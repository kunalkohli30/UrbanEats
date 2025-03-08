package com.urbaneats.dto.restaurant;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.urbaneats.model.*;
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
public class RestaurantRequestDto {

    private User owner;

    private String name;
    private String description;
    private String cuisineType;
    private String openingHours;
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime registrationDate = LocalDateTime.now();
    private boolean open;

    private Address address;

    private ContactInformation contactInformation;

    private List<Order> orders;

    private List<String> images;

    private List<Food> menu;    //Mapped as food by yt(youtube) guy
}
