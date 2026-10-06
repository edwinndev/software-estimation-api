package com.intecx.inscope.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Value;

@Value
@Schema(description = "Cuerpo uniforme de todos los errores")
public class ErrorResponse {

    @Schema(example = "404")
    int code;

    @Schema(example = "Proyecto no encontrado")
    String message;

    @Schema(example = "Not Found")
    String cause;

    @Schema(example = "GET http://localhost:8080/api/v1/projects/42")
    String path;

    LocalDateTime timestamp;

    public ErrorResponse(int code, String message, String cause, String path) {
        this.code = code;
        this.message = message;
        this.cause = cause;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}

