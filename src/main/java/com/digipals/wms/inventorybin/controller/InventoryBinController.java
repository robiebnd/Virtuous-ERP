package com.digipals.wms.inventorybin.controller;

import com.digipals.wms.inventorybin.dto.CreateInventoryBinRequest;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.dto.UpdateInventoryBinRequest;
import com.digipals.wms.inventorybin.service.InventoryBinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-bins")
@RequiredArgsConstructor
public class InventoryBinController {

    private final InventoryBinService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryBinResponse create(
            @Valid @RequestBody CreateInventoryBinRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public InventoryBinResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateInventoryBinRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    public List<InventoryBinResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public InventoryBinResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryBinResponse> getByWarehouse(
            @PathVariable UUID warehouseId) {

        return service.findByWarehouse(warehouseId);
    }

    @GetMapping("/bin/{binId}")
    public List<InventoryBinResponse> getByBin(
            @PathVariable UUID binId) {

        return service.findByBin(binId);
    }

    @GetMapping("/product/{productId}")
    public List<InventoryBinResponse> getByProduct(
            @PathVariable UUID productId) {

        return service.findByProduct(productId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}