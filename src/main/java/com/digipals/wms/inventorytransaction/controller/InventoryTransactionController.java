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

    /**
     * Get all inventory transactions.
     */
    @GetMapping
    public List<InventoryTransactionResponse> findAll() {

        return service.findAll()

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }

    /**
     * Get a transaction by ID.
     */
    @GetMapping("/{id}")
    public InventoryTransactionResponse findById(
            @PathVariable UUID id) {

        return InventoryTransactionMapper.toResponse(
                service.findById(id));
    }

    /**
     * Get all transactions for an Inventory Bin.
     */
    @GetMapping("/inventory-bin/{inventoryBinId}")
    public List<InventoryTransactionResponse> findByInventoryBin(
            @PathVariable UUID inventoryBinId) {

        return service.findByInventoryBin(
                        inventoryBinId)

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }

    /**
     * Get all transactions for a Bin.
     */
    @GetMapping("/bin/{binId}")
    public List<InventoryTransactionResponse> findByBin(
            @PathVariable UUID binId) {

        return service.findByBin(
                        binId)

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }

    /**
     * Find transactions by reference number.
     */
    @GetMapping("/reference/{referenceNumber}")
    public List<InventoryTransactionResponse> findByReferenceNumber(
            @PathVariable String referenceNumber) {

        return service.findByReferenceNumber(
                        referenceNumber)

                .stream()

                .map(InventoryTransactionMapper::toResponse)

                .toList();
    }
}