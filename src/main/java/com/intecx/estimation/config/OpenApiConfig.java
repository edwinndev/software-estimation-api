package com.intecx.estimation.config;

import com.intecx.estimation.exception.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ERROR_SCHEMA = "ErrorResponse";

    private static final Map<String, String> ERROR_RESPONSES = Map.of(
            "400", "Solicitud inválida o datos de entrada con errores",
            "500", "Error interno del servidor"
    );

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Software Estimation API")
                        .version("v1")
                        .description("API REST de estimación de software"))
                .components(new Components().schemas(errorSchemas()));
    }

    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation ->
                        ERROR_RESPONSES.forEach((status, description) -> {
                            if (operation.getResponses().get(status) == null) {
                                operation.getResponses().addApiResponse(status, errorResponse(description));
                            }
                        })));
    }

    private static Map<String, Schema> errorSchemas() {
        return ModelConverters.getInstance().readAll(ErrorResponse.class);
    }

    private static ApiResponse errorResponse(String description) {
        Schema<?> reference = new Schema<>().$ref("#/components/schemas/" + ERROR_SCHEMA);
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json", new MediaType().schema(reference)));
    }
}
