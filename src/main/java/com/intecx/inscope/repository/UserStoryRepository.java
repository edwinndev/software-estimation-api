package com.intecx.inscope.repository;

import com.intecx.inscope.entity.UserStory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {
}