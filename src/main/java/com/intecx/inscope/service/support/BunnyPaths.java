package com.intecx.inscope.service.support;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BunnyPaths {

    public static final String AVATARS = "avatars";
    public static final String REPORTS = "reports";

    public String avatar(UUID userId, String extension) {
        return AVATARS + "/" + userId + "." + extension;
    }

    public String reportPdf(UUID estimationId) {
        return REPORTS + "/" + estimationId + ".pdf";
    }
}
