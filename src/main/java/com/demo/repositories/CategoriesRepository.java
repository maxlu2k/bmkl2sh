package com.demo.repositories;

import com.demo.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
}
