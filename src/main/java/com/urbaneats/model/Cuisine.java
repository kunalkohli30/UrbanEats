package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuisine {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String cuisineName;


    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
