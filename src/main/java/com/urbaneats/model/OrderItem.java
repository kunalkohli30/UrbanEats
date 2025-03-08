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

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_food"))  // Foreign Key in order_item table
    private Food food;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)  // Foreign Key to Order
    private Order order;

    private int quantity;
    private int totalPrice;
}
