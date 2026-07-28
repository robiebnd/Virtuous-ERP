package com.digipals.wms.common.mapper;

import com.digipals.wms.users.dto.WarehouseAssignmentResponse;
import com.digipals.wms.users.entity.WarehouseAssignment;

public final class WarehouseAssignmentMapper {

    private WarehouseAssignmentMapper() {
    }

    public static WarehouseAssignmentResponse toResponse(
            WarehouseAssignment assignment) {

        return WarehouseAssignmentResponse.builder()

                .id(
                        assignment.getId())

                .userId(
                        assignment.getUser().getId())

                .username(
                        assignment.getUser().getUsername())

                .warehouseId(
                        assignment.getWarehouse().getId())

                .warehouseCode(
                        assignment.getWarehouse().getCode())

                .warehouseName(
                        assignment.getWarehouse().getName())

                .primaryWarehouse(
                        assignment.getPrimaryWarehouse())

                .createdAt(
                        assignment.getCreatedAt())

                .build();
    }
}