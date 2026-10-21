package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.FileAttachmentResponse;
import com.intecx.inscope.entity.FileAttachment;
import org.springframework.stereotype.Component;

@Component
public class FileAttachmentMapper implements BaseMapper<FileAttachment, FileAttachmentResponse> {

    @Override
    public FileAttachmentResponse toResponse(FileAttachment entity) {
        if (entity == null) {
            return null;
        }
        return FileAttachmentResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .fileName(entity.getFileName())
                .contentType(entity.getContentType())
                .sizeBytes(entity.getSizeBytes())
                .storagePath(entity.getStoragePath())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
