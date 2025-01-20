package com.urbaneats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {

    private Long id;
    private String streetAddress;
    private String areaName;
    private String city;
    private String zipCode;
    private String country = "India";
    private String addressName;
    private String userId;


}
