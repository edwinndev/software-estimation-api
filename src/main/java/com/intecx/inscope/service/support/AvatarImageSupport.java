package com.intecx.inscope.service.support;

import com.intecx.inscope.exception.BadRequestException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class AvatarImageSupport {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/jpg", "jpg"
    );

    public String resolveExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Debes adjuntar una imagen de avatar");
        }
        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Solo se permiten imágenes PNG, JPEG o JPG");
        }
        String fromName = extensionFromFilename(file.getOriginalFilename());
        if (fromName != null) {
            return fromName;
        }
        return EXTENSION_BY_CONTENT_TYPE.get(contentType);
    }

    public String normalizeContentType(String contentType, String extension) {
        if ("png".equals(extension)) {
            return "image/png";
        }
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "image/jpeg";
        }
        return contentType == null ? "application/octet-stream" : contentType;
    }

    private String extensionFromFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).trim().toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "png" -> "png";
            case "jpg", "jpeg" -> "jpg";
            default -> null;
        };
    }
}
