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
public class Offers {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String header;
    private String couponCode;
    private String description;
    private String offerLogo;
    private String offerType;   // absolute amount or percentage
    private Integer discountPercentage;
    private Integer discountAmount;
    private Integer minimumOrderValue;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;
}
