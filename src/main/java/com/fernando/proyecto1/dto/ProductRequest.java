package com.fernando.proyecto1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;

@Data
public class ProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Hamburguesa")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Schema(description = "Precio del producto", example = "9.99")
    private BigDecimal price;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean active = true;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "ID de la categoría", example = "1")
    private Long categoryId;
}