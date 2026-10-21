package com.intecx.inscope.rest;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Map<String, String> health() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return Map.of("status", "UP", "database", "UP");
    }
}