package com.urbaneats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String streetAddress;
    private String areaName;
    private String city;
    private String zipCode;
    private String country;
    private String addressName;

//    @OneToMany(mappedBy = "deliveryAddress", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Order> a_orders;

    private String userId;
}
