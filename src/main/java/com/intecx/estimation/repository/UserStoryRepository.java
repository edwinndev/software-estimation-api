package com.intecx.estimation.repository;

import com.intecx.estimation.entity.UserStory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {
}