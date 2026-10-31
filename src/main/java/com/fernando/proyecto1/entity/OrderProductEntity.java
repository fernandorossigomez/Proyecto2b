package com.fernando.proyecto1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private OrderEntity order;

    @ManyToOne
    private ProductEntity product;

    private Integer quantity;

    private Double price;
}