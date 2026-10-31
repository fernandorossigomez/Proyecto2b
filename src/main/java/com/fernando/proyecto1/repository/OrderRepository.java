package com.fernando.proyecto1.repository;

import com.fernando.proyecto1.entity.OrderEntity;
import com.fernando.proyecto1.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStatus(OrderStatus status);
    Optional<OrderEntity> findByCode(String code);

}
