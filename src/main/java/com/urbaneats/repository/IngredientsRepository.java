package com.urbaneats.repository;

import com.urbaneats.model.IngredientsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepository extends JpaRepository<IngredientsItem, Long> {

    IngredientsItem findByName(String name);
}
