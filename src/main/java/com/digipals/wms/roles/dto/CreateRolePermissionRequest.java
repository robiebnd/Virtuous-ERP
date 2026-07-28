package com.digipals.wms.roles.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateRolePermissionRequest {

    private UUID roleId;

    private UUID permissionId;
}