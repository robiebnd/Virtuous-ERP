package com.digipals.wms.inventorytransaction.controller;

import com.digipals.wms.common.mapper.InventoryTransactionMapper;
import com.digipals.wms.inventorytransaction.dto.InventoryTransactionResponse;
import com.digipals.wms.inventorytransaction.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService service;

    @GetMapping
    public List<InventoryTransactionResponse> findAll() {

        return service.findAll()

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }

    @GetMapping("/{id}")
    public InventoryTransactionResponse findById(
            @PathVariable UUID id) {

        return InventoryTransactionMapper.toResponse(
                service.findById(id));
    }

    @GetMapping("/inventory/{inventoryId}")
    public List<InventoryTransactionResponse> findByInventory(
            @PathVariable UUID inventoryId) {

        return service.findByInventory(inventoryId)

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }
}