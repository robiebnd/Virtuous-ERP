package com.digipals.wms.inventory.controller;

import com.digipals.wms.common.mapper.InventoryMapper;
import com.digipals.wms.inventory.dto.InventoryResponse;
import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    public InventoryResponse create(
            @RequestBody Inventory inventory) {

        Inventory saved =
                service.create(inventory);

        return InventoryMapper.toResponse(saved);
    }

    @GetMapping
    public List<InventoryResponse> getAll() {

        return service.findAll()
                .stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public InventoryResponse getById(
            @PathVariable UUID id) {

        return InventoryMapper.toResponse(
                service.findById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryResponse> getByWarehouse(
            @PathVariable UUID warehouseId) {

        return service.findByWarehouse(warehouseId)
                .stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/product/{productId}")
    public List<InventoryResponse> getByProduct(
            @PathVariable UUID productId) {

        return service.findByProduct(productId)
                .stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/adjust")
    public InventoryResponse adjustStock(
            @PathVariable UUID id,
            @RequestParam BigDecimal quantity) {

        return InventoryMapper.toResponse(
                service.adjustStock(id, quantity));
    }
}