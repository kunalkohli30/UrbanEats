package com.urbaneats.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.urbaneats.model.Address;
import com.urbaneats.model.Order;
import com.urbaneats.model.RestaurantDto;
import com.urbaneats.model.USER_ROLE;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String uid;
    private String fullName;
    private String email;
    private USER_ROLE user_role;
    private List<Order> customer_orders;
    private List<RestaurantDto> favourites;
    private List<Address> addresses;
    private String imageUrl;
    private String phoneNumber;
    private String password;
}
