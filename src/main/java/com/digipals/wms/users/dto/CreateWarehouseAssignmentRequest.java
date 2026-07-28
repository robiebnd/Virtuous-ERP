package com.digipals.wms.users.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateWarehouseAssignmentRequest {

    private UUID userId;

    private UUID warehouseId;

    private Boolean primaryWarehouse;
}