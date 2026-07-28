package com.digipals.wms.common.mapper;

import com.digipals.wms.roles.dto.PermissionResponse;
import com.digipals.wms.roles.entity.Permission;

public final class PermissionMapper {

    private PermissionMapper() {
    }

    public static PermissionResponse toResponse(
            Permission permission) {

        return PermissionResponse.builder()

                .id(permission.getId())

                .code(permission.getCode())

                .description(permission.getDescription())

                .createdAt(permission.getCreatedAt())

                .build();
    }
}