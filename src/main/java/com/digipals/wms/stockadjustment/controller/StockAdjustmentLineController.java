package com.digipals.wms.stockadjustment.controller;

import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentLineRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentLineResponse;
import com.digipals.wms.stockadjustment.service.StockAdjustmentLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-adjustment-lines")
@RequiredArgsConstructor
public class StockAdjustmentLineController {

    private final StockAdjustmentLineService service;

    @PostMapping
    public StockAdjustmentLineResponse create(
            @RequestBody CreateStockAdjustmentLineRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<StockAdjustmentLineResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public StockAdjustmentLineResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/adjustment/{adjustmentId}")
    public List<StockAdjustmentLineResponse> getByAdjustment(
            @PathVariable UUID adjustmentId) {

        return service.findByAdjustment(adjustmentId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}
