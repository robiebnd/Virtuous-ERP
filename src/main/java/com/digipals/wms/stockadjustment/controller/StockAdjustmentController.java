package com.digipals.wms.stockadjustment.controller;

import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockadjustment.service.StockAdjustmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-adjustments")
@RequiredArgsConstructor
public class StockAdjustmentController {

    private final StockAdjustmentService service;

    @PostMapping
    public StockAdjustmentResponse create(
            @RequestBody CreateStockAdjustmentRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<StockAdjustmentResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public StockAdjustmentResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @PutMapping("/{id}/approve")
    public StockAdjustmentResponse approve(
            @PathVariable UUID id) {

        return service.approve(id);
    }

    @PutMapping("/{id}/post")
    @PreAuthorize("hasAuthority('STOCK_ADJUSTMENT_POST')")
    public StockAdjustmentResponse post(
            @PathVariable UUID id) {

        return service.post(id);
    }
}