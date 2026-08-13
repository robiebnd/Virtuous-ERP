package com.digipals.wms.stockcount.controller;

import com.digipals.wms.stockcount.dto.CreateStockCountLineRequest;
import com.digipals.wms.stockcount.dto.StockCountLineResponse;
import com.digipals.wms.stockcount.service.StockCountLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-count-lines")
@RequiredArgsConstructor
public class StockCountLineController {

    private final StockCountLineService service;

    @GetMapping
    public List<StockCountLineResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public StockCountLineResponse getById(
            @PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/count/{countId}")
    public List<StockCountLineResponse> getByStockCount(
            @PathVariable UUID countId) {
        return service.findByStockCount(countId);
    }

    @PutMapping("/{id}/count")
    public StockCountLineResponse updateCount(
            @PathVariable UUID id,
            @Valid @RequestBody CreateStockCountLineRequest request) {
        return service.updateCount(id, request);
    }

    @PostMapping
    public StockCountLineResponse create(
            @Valid @RequestBody CreateStockCountLineRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {
        service.delete(id);
    }
}
