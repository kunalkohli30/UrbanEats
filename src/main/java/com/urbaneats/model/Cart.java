package com.urbaneats.model;

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
public class Cart {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private Integer total;
    private String customerId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CartItem> cartItems;


}
