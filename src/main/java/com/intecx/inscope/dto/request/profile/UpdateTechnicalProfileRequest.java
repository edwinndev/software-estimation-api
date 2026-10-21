package com.intecx.inscope.dto.request.profile;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTechnicalProfileRequest extends CreateTechnicalProfileRequest {

    private UUID id;
}
