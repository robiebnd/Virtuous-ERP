package com.digipals.wms.users.service;

import com.digipals.wms.users.dto.CreateWarehouseAssignmentRequest;
import com.digipals.wms.users.dto.WarehouseAssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface WarehouseAssignmentService {

    WarehouseAssignmentResponse assignWarehouse(
            CreateWarehouseAssignmentRequest request);

    List<WarehouseAssignmentResponse> findAll();

    List<WarehouseAssignmentResponse> findByUser(
            UUID userId);

    void removeWarehouse(
            UUID userId,
            UUID warehouseId);
}