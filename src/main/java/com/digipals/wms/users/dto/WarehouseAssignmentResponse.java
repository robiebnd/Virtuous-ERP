package com.digipals.wms.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WarehouseAssignmentResponse {

    private UUID id;

    private UUID userId;

    private String username;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private Boolean primaryWarehouse;

    private LocalDateTime createdAt;
}