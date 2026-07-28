package com.digipals.wms.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digipals.wms.roles.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository
        extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}