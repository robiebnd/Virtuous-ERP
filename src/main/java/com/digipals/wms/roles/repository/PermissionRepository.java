package com.digipals.wms.roles.repository;

import com.digipals.wms.roles.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(
            String code);

    boolean existsByCode(
            String code);

     boolean existsByName(String name);

    Optional<Permission> findByName(String name);
}