package com.jubhub.respository;

import com.jubhub.model.entity.Role;
import com.jubhub.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleType name);
    boolean existsByName(RoleType name);
}

