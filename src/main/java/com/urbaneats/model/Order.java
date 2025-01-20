package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orderr")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String orderStatus;
    private Long totalAmount;
    private LocalDate createdAt;
    private int totalItem;
    private int totalPrice;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
