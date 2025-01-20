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
public class IngredientCategory {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    @Column(unique = true)
    private String name;

//    @ManyToOne
//    @JsonIgnore
//    private Restaurant restaurant;

//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    private List<IngredientsItem> ingredients;
}
