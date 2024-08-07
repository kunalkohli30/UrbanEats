package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.urbaneats.dto.RestaurantDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private USER_ROLE user_role;

    @JsonIgnore
    @OneToMany
    private List<Order> orders;

    private List<RestaurantDto> favourites;

    private List<Address> addresses;
}
