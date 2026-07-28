package com.digipals.wms.roles.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RolePermissionResponse {

    private UUID id;

    private UUID roleId;

    private String roleName;

    private UUID permissionId;

    private String permissionCode;

    private String permissionDescription;

    private LocalDateTime createdAt;
}