package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CartItem {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    @ManyToOne()
    @JsonIgnore
//    @JoinColumn(name = "cartItems")
    private Cart cart;


    private int quantity;
//    Skipped ingredients from video
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
}
