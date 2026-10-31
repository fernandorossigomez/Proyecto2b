package com.fernando.proyecto1.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    @Positive
    @Schema(description = "Total del pedido", example = "12.5")
    private Double total;

    @NotNull
    @Schema(description = "ID del terminal", example = "1")
    private Long terminalId;
}
