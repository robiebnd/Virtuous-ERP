package com.digipals.wms.common.mapper;

import com.digipals.wms.roles.dto.RolePermissionResponse;
import com.digipals.wms.roles.entity.RolePermission;


public final class RolePermissionMapper {

    private RolePermissionMapper() {
    }

    public static RolePermissionResponse toResponse(
            RolePermission rolePermission) {

        return RolePermissionResponse.builder()

                .id(
                        rolePermission.getId())

                .roleId(
                        rolePermission.getRole().getId())

                .roleName(
                        rolePermission.getRole().getName())

                .permissionId(
                        rolePermission.getPermission().getId())

                .permissionCode(
                        rolePermission.getPermission().getCode())

                .permissionDescription(
                        rolePermission.getPermission().getDescription())

                .createdAt(
                        rolePermission.getCreatedAt())

                .build();
    }
}
