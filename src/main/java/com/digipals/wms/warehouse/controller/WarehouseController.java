package com.digipals.wms.warehouse.controller;

import com.digipals.wms.common.mapper.WarehouseMapper;
import com.digipals.wms.warehouse.dto.CreateWarehouseRequest;
import com.digipals.wms.warehouse.dto.UpdateWarehouseRequest;
import com.digipals.wms.warehouse.dto.WarehouseResponse;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @PostMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CREATE')")
    public WarehouseResponse create(
            @Valid
            @RequestBody CreateWarehouseRequest request) {

        Warehouse warehouse =
                service.create(request);

        return WarehouseMapper.toResponse(
                warehouse);
    }

    @GetMapping
    public List<WarehouseResponse> getAll() {

        return service.getAll()
                .stream()
                .map(WarehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WarehouseResponse getById(
            @PathVariable UUID id) {

        return WarehouseMapper.toResponse(
                service.getById(id));
    }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE')")
        public WarehouseResponse update(
                @PathVariable UUID id,
                @Valid
                @RequestBody UpdateWarehouseRequest request) {

        Warehouse warehouse =
                service.update(id, request);

        return WarehouseMapper.toResponse(
                warehouse);
        }



}