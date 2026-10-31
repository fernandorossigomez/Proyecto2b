package com.fernando.proyecto1.repository;

import com.fernando.proyecto1.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ProductEntity> findByPriceBetween(Double min, Double max);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    List<ProductEntity> findByPriceGreaterThanEqual(Double minPrice);

    List<ProductEntity> findByPriceLessThanEqual(Double maxPrice);

    java.util.Optional<ProductEntity> findByNameIgnoreCase(String name);
}