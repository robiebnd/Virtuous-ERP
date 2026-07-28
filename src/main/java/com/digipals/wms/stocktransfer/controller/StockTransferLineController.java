package com.digipals.wms.stocktransfer.controller;

import com.digipals.wms.stocktransfer.dto.CreateStockTransferLineRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferLineResponse;
import com.digipals.wms.stocktransfer.service.StockTransferLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-transfer-lines")
@RequiredArgsConstructor
public class StockTransferLineController {

    private final StockTransferLineService service;

    /**
     * Create Stock Transfer Line
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockTransferLineResponse create(
            @Valid @RequestBody CreateStockTransferLineRequest request) {

        return service.create(request);
    }

    /**
     * Get All Stock Transfer Lines
     */
    @GetMapping
    public List<StockTransferLineResponse> getAll() {

        return service.findAll();
    }

    /**
     * Get Stock Transfer Line By Id
     */
    @GetMapping("/{id}")
    public StockTransferLineResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    /**
     * Get Lines For Stock Transfer
     */
    @GetMapping("/stock-transfer/{stockTransferId}")
    public List<StockTransferLineResponse> getByStockTransfer(
            @PathVariable UUID stockTransferId) {

        return service.findByStockTransferId(stockTransferId);
    }

    /**
     * Delete Stock Transfer Line
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}