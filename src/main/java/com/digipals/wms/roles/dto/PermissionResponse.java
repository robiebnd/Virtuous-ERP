package com.digipals.wms.roles.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PermissionResponse {

    private UUID id;

    private String code;

    private String description;

    private LocalDateTime createdAt;
}