package com.digipals.wms.inventory.reconciliation.controller;

import com.digipals.wms.common.mapper.InventoryBinMapper;
import com.digipals.wms.inventory.reconciliation.dto.InventoryReconciliationRequest;
import com.digipals.wms.inventory.reconciliation.service.InventoryReconciliationService;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/reconciliation")
@RequiredArgsConstructor
public class InventoryReconciliationController {

    private final InventoryReconciliationService service;

    @PostMapping
    public InventoryBinResponse reconcile(
            @Valid @RequestBody InventoryReconciliationRequest request) {

        return InventoryBinMapper.toResponse(service.reconcile(request));
    }
}
