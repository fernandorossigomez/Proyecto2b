package com.fernando.proyecto1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderProductResponse {

    private Long productId;
    private String name;
    private Integer quantity;
    private Double price;
}