package com.intecx.inscope.repository;

import com.intecx.inscope.entity.FileAttachment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {
}
