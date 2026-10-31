package com.fernando.proyecto1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSalesResponse {

    private String name;
    private Integer totalSold;
}