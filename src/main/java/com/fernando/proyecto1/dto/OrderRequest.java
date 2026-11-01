package com.fernando.proyecto1.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {

    @NotNull
    @Positive
    @Schema(description = "Total del pedido", example = "12.5")
    private BigDecimal total;

    @NotNull
    @Positive
    @Schema(description = "ID del terminal", example = "1")
    private Long terminalId;
}
