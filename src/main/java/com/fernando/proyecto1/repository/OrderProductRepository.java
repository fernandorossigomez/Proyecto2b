package com.fernando.proyecto1.repository;

import com.fernando.proyecto1.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long> {
}