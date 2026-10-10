package com.intecx.inscope.config;

import com.intecx.inscope.exception.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ERROR_SCHEMA = "ErrorResponse";
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    private static final Map<String, String> ERROR_RESPONSES = Map.of(
            "400", "Solicitud inválida o datos de entrada con errores",
            "401", "No autorizado o credenciales inválidas",
            "403", "Acceso denegado o permisos insuficientes",
            "500", "Error interno del servidor"
    );

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Software Estimation API (InScope)")
                        .version("v1")
                        .description("API REST de estimación de software con autenticación JWT"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ErrorResponse.class);
            schemas.forEach(openApi.getComponents()::addSchemas);

            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem ->
                        pathItem.readOperations().forEach(operation ->
                                ERROR_RESPONSES.forEach((status, description) -> {
                                    if (operation.getResponses().get(status) == null) {
                                        operation.getResponses().addApiResponse(status, errorResponse(description));
                                    }
                                })));
            }
        };
    }

    private static ApiResponse errorResponse(String description) {
        Schema<?> reference = new Schema<Object>().$ref("#/components/schemas/" + ERROR_SCHEMA);
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json", new MediaType().schema(reference)));
    }
}
