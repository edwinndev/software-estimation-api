package com.intecx.estimation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StoryPointsRequest(
        @NotNull(message = "Los story points son obligatorios")
        @Positive(message = "Los story points deben ser un número positivo")
        @Max(value = Short.MAX_VALUE, message = "Los story points superan el máximo permitido")
        Integer storyPoints
) {
}