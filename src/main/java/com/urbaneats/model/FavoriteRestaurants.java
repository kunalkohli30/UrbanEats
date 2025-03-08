package com.urbaneats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

@Entity
@Table(name = "FavoriteRestaurants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "restaurant_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRestaurants {

    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @Id
    private Long id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 500)
    private String notes;  // Optional user notes on why they favorited the restaurant

}
