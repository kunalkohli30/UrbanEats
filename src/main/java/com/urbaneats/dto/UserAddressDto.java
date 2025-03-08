package com.urbaneats.dto;

import com.urbaneats.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {

    private String formattedGoogleAddress;
    private String lat;
    private String lng;
    private Long userAddressId;
    private String flatNo;
    private String street;
    private String city;
    private String pinCode;
    private String addressName;
    private String userId;

}
