package com.urbaneats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddress {

    @GeneratedValue(
            strategy= GenerationType.IDENTITY
    )
    @Id
    private Long userAddressId;

    private String flatNo;
    private String street;
    private String city;
    private String pinCode;
    private String addressName;

    @Column(name = "formatted_google_address")
    private String formattedGoogleAddress;
    private String lat;
    private String lng;
    private String userId;

}
