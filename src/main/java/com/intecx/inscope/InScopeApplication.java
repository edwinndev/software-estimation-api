package com.intecx.inscope;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableScheduling
public class InScopeApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    static void main(String[] args) {
        if (isDevProfile()) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null && System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        }

        SpringApplication.run(InScopeApplication.class, args);
    }

    private static boolean isDevProfile() {
        String active = System.getenv("SPRING_PROFILES_ACTIVE");
        if (!StringUtils.hasText(active)) {
            active = System.getProperty("spring.profiles.active", "dev");
        }
        return active.contains("dev");
    }
}
