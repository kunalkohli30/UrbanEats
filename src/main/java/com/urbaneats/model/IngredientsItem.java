package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientsItem {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String name;
    private boolean inStock = true;

    @ManyToOne
    private IngredientCategory category;

    @ManyToMany
    private List<Food> food;

    //    @ManyToOne
//    @JsonIgnore
//    private Restaurant restaurant;

}
