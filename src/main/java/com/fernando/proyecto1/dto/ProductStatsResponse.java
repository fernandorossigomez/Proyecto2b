package com.fernando.proyecto1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStatsResponse {

    private String name;
    private Integer totalSold;

}