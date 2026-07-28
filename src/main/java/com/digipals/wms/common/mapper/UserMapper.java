package com.digipals.wms.common.mapper;

import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.entity.RolePermission;
import com.digipals.wms.users.dto.UserResponse;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {

        Set<String> roles = user.getUserRoles()

                .stream()

                .map(UserRole::getRole)

                .map(Role::getName)

                .collect(Collectors.toSet());

        Set<String> permissions = user.getUserRoles()

                .stream()

                .map(UserRole::getRole)

                .flatMap(role -> role.getRolePermissions().stream())

                .map(RolePermission::getPermission)

                .map(Permission::getCode)

                .collect(Collectors.toSet());

        return UserResponse.builder()

                .id(user.getId())

                .username(user.getUsername())

                .email(user.getEmail())

                .firstName(user.getFirstName())

                .lastName(user.getLastName())

                .phoneNumber(user.getPhoneNumber())

                .enabled(user.getEnabled())

                .accountLocked(user.getAccountLocked())

                .warehouseId(
                        user.getDefaultWarehouse() == null
                                ? null
                                : user.getDefaultWarehouse().getId())

                .warehouseCode(
                        user.getDefaultWarehouse() == null
                                ? null
                                : user.getDefaultWarehouse().getCode())

                .warehouseName(
                        user.getDefaultWarehouse() == null
                                ? null
                                : user.getDefaultWarehouse().getName())

                .roles(roles)

                .permissions(permissions)

                .createdAt(user.getCreatedAt())

                .updatedAt(user.getUpdatedAt())

                .build();
    }
}