package com.digipals.wms.common.mapper;

import com.digipals.wms.roles.dto.RoleResponse;
import com.digipals.wms.roles.entity.Role;

import lombok.Builder;


@Builder
public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse toResponse(
            Role role) {

        return RoleResponse.builder()

                .id(
                        role.getId())

                .name(
                        role.getName())

                .description(
                        role.getDescription())

                .createdAt(
                        role.getCreatedAt())

                .build();
    }
}