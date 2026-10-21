package com.intecx.inscope.repository;

import com.intecx.inscope.entity.Role;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByIsActiveTrue();
}
