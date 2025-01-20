package com.urbaneats.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodTypes {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )    @Id
    private Long id;

    private String name;
    private String imageId;
}
