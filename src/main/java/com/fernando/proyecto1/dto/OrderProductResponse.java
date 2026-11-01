package com.fernando.proyecto1.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderProductResponse {

    private Long productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}