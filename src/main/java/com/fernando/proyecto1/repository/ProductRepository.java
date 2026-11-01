package com.fernando.proyecto1.repository;

import com.fernando.proyecto1.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ProductEntity> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    List<ProductEntity> findByPriceGreaterThanEqual(BigDecimal minPrice);

    List<ProductEntity> findByPriceLessThanEqual(BigDecimal maxPrice);

    java.util.Optional<ProductEntity> findByNameIgnoreCase(String name);
}