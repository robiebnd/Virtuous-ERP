package com.digipals.wms.users.controller;

import com.digipals.wms.users.dto.CreateWarehouseAssignmentRequest;
import com.digipals.wms.users.dto.WarehouseAssignmentResponse;
import com.digipals.wms.users.service.WarehouseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse-assignments")
@RequiredArgsConstructor
public class WarehouseAssignmentController {

    private final WarehouseAssignmentService service;

    @PostMapping
    public WarehouseAssignmentResponse assignWarehouse(
            @RequestBody CreateWarehouseAssignmentRequest request) {

        return service.assignWarehouse(request);
    }

    @GetMapping
    public List<WarehouseAssignmentResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<WarehouseAssignmentResponse> findByUser(
            @PathVariable UUID userId) {

        return service.findByUser(userId);
    }

    @DeleteMapping
    public void removeWarehouse(
            @RequestParam UUID userId,
            @RequestParam UUID warehouseId) {

        service.removeWarehouse(
                userId,
                warehouseId);
    }
}