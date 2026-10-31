package com.fernando.proyecto1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class ErrorResponse {

    @Schema(description = "Mensaje del error", example = "Terminal no encontrada")
    private String error;

    @Schema(description = "Código HTTP", example = "404")
    private int status;

    private String timestamp;

    public ErrorResponse(String error, int status) {
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}