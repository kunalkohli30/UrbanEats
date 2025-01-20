package com.urbaneats.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Food {

    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @Id
    private Long id;

    private String name;

    @Column(length = 500)
    private String description;
    private Integer price;
    private boolean available;
    private boolean isVegetarian;
    private boolean isSeasonal;
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category foodCategory;

//    @Column(length = 1000)
//    @ElementCollection
//    private List<String> images;

    private String imageId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Restaurant restaurant;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<IngredientsItem> ingredients;

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", available=" + available +
                ", isVegetarian=" + isVegetarian +
                ", isSeasonal=" + isSeasonal +
                ", creationDate=" + creationDate +
                ", foodCategory=" + foodCategory +
//                ", images=" + images +
                ", restaurant=" + restaurant +
                ", ingredients=" + ingredients +
                '}';
    }
}


