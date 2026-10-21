package com.intecx.inscope.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "in-scope.bunny")
public class BunnyProperties {

    private String storageHost;
    private String storageZone;
    private String accessKey;
}
