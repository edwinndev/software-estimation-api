package com.intecx.estimation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record TaskResponsiblesRequest(
        @NotNull List<@NotNull UUID> profileIds
) {
}
