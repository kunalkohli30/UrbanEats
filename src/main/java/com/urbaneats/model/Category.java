package com.urbaneats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//@JsonIgnoreProperties("foodItems")
public class Category {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    @Column(unique = true)
    private String name;

//    private String superCategory;

//    @JsonIgnore
//    @ManyToOne
//    private Restaurant restaurant;

//    @OneToMany(mappedBy = "foodCategory", fetch = FetchType.LAZY)
//    private List<Food> foodItems;
}
