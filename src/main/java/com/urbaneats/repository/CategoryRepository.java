package com.urbaneats.repository;

import com.urbaneats.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Optional<Category> findByName(String name);
}
