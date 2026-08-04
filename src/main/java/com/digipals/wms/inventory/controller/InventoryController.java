package com.digipals.wms.inventory.controller;

import com.digipals.wms.common.mapper.InventoryBinMapper;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    public InventoryBinResponse create(
            @RequestBody InventoryBin inventoryBin) {

        return InventoryBinMapper.toResponse(
                service.create(inventoryBin));
    }

    @GetMapping
    public List<InventoryBinResponse> findAll() {

        return service.findAll()

                .stream()

                .map(InventoryBinMapper::toResponse)

                .toList();
    }

    @GetMapping("/{id}")
    public InventoryBinResponse findById(
            @PathVariable UUID id) {

        return InventoryBinMapper.toResponse(
                service.findById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryBinResponse> findByWarehouse(
            @PathVariable UUID warehouseId) {

        return service.findByWarehouse(
                        warehouseId)

                .stream()

                .map(InventoryBinMapper::toResponse)

                .toList();
    }

    @GetMapping("/product/{productId}")
    public List<InventoryBinResponse> findByProduct(
            @PathVariable UUID productId) {

        return service.findByProduct(
                        productId)

                .stream()

                .map(InventoryBinMapper::toResponse)

                .toList();
    }

    @PutMapping("/{id}/adjust")
    public InventoryBinResponse adjustStock(
            @PathVariable UUID id,
            @RequestParam BigDecimal quantity) {

        return InventoryBinMapper.toResponse(
                service.adjustStock(
                        id,
                        quantity));
    }

    @GetMapping("/warehouse/{warehouseId}/bin/{binId}/product/{productId}")
    public InventoryBinResponse getInventory(
            @PathVariable UUID warehouseId,
            @PathVariable UUID binId,
            @PathVariable UUID productId) {

        return InventoryBinMapper.toResponse(

                service.getInventory(

                        warehouseId,

                        binId,

                        productId));
    }

    @GetMapping("/{id}/available")
    public BigDecimal availableStock(
            @PathVariable UUID id) {

        return service.availableStock(id);
    }
}