package com.digipals.wms.stockcount.controller;

import com.digipals.wms.stockcount.dto.CreateStockCountRequest;
import com.digipals.wms.stockcount.dto.StockCountResponse;
import com.digipals.wms.stockcount.service.StockCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-counts")
@RequiredArgsConstructor
public class StockCountController {

    private final StockCountService service;

    @PostMapping
    public StockCountResponse create(
            @RequestBody CreateStockCountRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<StockCountResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public StockCountResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @PostMapping("/{id}/load-inventory")
    public StockCountResponse loadInventory(
            @PathVariable UUID id) {

        return service.loadInventory(id);
    }

    @PutMapping("/{id}/complete")
    public StockCountResponse complete(
            @PathVariable UUID id) {

        return service.complete(id);
    }

    @PostMapping("/{id}/generate-adjustment")
    public StockCountResponse generateAdjustment(
            @PathVariable UUID id) {

        return service.generateAdjustment(id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}