package com.digipals.wms.roles.repository;

import com.digipals.wms.roles.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.entity.Permission;
import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleId(
            UUID roleId);

    List<RolePermission> findByPermissionId(
            UUID permissionId);

    List<RolePermission> findByRole(Role role);

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);

        boolean existsByRoleAndPermission(
        Role role,
        Permission permission);

    void deleteByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);


    void deleteByRole(Role role);

    long countByRoleId(
            UUID roleId);
}