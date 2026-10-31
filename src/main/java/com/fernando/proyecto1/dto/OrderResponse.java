package com.fernando.proyecto1.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private List<OrderProductResponse> products;
    private Long id;
    private String code;
    private LocalDateTime date;
    private Double total;
    private String status;

}
