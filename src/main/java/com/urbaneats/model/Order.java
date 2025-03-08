package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDateTime createdAt;
    private Integer deliveryTimeInSeconds;
    private Integer totalItem;
    private Integer totalPrice;
    private Integer deliveryFee;
    private Integer deliveryTip;
    private Integer gstAndFees;
    private String couponCode;
    private Integer discountAmount;
    private Integer totalAmount;

    private String userId;
    private String razorpayOrderId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String deliveryAddress;
    private String addressName;

//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<OrderItem> items = new ArrayList<>();
//
//    public void addItem(OrderItem item) {
//        items.add(item);
//        item.setOrder(this); // Ensure bidirectional consistency
//    }
}
