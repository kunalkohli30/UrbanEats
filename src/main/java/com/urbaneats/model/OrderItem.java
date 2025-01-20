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
public class OrderItem {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    @OneToOne
    private Food food;

    @ManyToOne
    private Order order;

    private int quantity;
    private Long totalPrice;
    private List<String> ingredients;
}
