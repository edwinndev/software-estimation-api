package com.intecx.inscope.service.impl;

import com.intecx.inscope.config.BunnyProperties;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.service.BunnyStorageService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BunnyStorageServiceImpl implements BunnyStorageService {

    private static final String ACCESS_KEY_HEADER = "AccessKey";

    private final BunnyProperties bunnyProperties;

    @Override
    public void upload(String storagePath, byte[] content, String contentType) {
        requireConfigured();
        try {
            client().put()
                    .uri(objectUri(storagePath))
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.error("Error al subir archivo a Bunny ({}): {}", storagePath, ex.getMessage());
            throw new BadRequestException("No se pudo almacenar el archivo del reporte");
        }
    }

    @Override
    public byte[] download(String storagePath) {
        requireConfigured();
        try {
            byte[] body = client().get()
                    .uri(objectUri(storagePath))
                    .retrieve()
                    .body(byte[].class);
            if (body == null || body.length == 0) {
                throw new BadRequestException("El archivo del reporte está vacío o no existe");
            }
            return body;
        } catch (RestClientResponseException ex) {
            log.error("Error al descargar archivo de Bunny ({}): {}", storagePath, ex.getMessage());
            throw new BadRequestException("No se pudo descargar el archivo del reporte");
        }
    }

    @Override
    public void delete(String storagePath) {
        if (!isConfigured() || !StringUtils.hasText(storagePath)) {
            return;
        }
        try {
            client().delete()
                    .uri(objectUri(storagePath))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.warn("No se pudo eliminar el archivo en Bunny ({}): {}", storagePath, ex.getMessage());
        }
    }

    private RestClient client() {
        return RestClient.builder()
                .defaultHeader(ACCESS_KEY_HEADER, bunnyProperties.getAccessKey())
                .build();
    }

    private URI objectUri(String storagePath) {
        String normalized = storagePath.startsWith("/") ? storagePath.substring(1) : storagePath;
        String url = "https://%s/%s/%s".formatted(
                bunnyProperties.getStorageHost(),
                bunnyProperties.getStorageZone(),
                normalized
        );
        return URI.create(url);
    }

    private void requireConfigured() {
        if (!isConfigured()) {
            throw new BadRequestException("El almacenamiento BunnyCDN no está configurado");
        }
    }

    private boolean isConfigured() {
        return StringUtils.hasText(bunnyProperties.getStorageHost())
                && StringUtils.hasText(bunnyProperties.getStorageZone())
                && StringUtils.hasText(bunnyProperties.getAccessKey());
    }
}
