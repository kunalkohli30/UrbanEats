package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    @OneToOne
    private User owner;

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

    private String openingHours;
    private LocalDateTime registrationDate;
    private String instagram;

//    @OneToOne
//    private Address address;

//    @Embedded
//    private ContactInformation contactInformation;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @ElementCollection
    @Column(length = 1000)
    private List<String> images;


//    @JsonIgnore
//    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
//    private List<Food> menu;    //Mapped as food by yt(youtube) guy


}
