package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String fullName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private USER_ROLE user_role;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> customer_orders;

    @ElementCollection
    private List<RestaurantDto> favourites;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Address> addresses;

    @OneToOne
    private Cart cart;
}
