package com.fernando.proyecto1.repository;

import com.fernando.proyecto1.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}