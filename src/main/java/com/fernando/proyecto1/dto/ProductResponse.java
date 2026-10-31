package com.fernando.proyecto1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private Boolean active;
    private Long categoryId;
    private String categoryName;
}