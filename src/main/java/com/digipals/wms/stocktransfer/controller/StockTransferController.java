package com.digipals.wms.stocktransfer.controller;

import com.digipals.wms.stocktransfer.dto.CreateStockTransferRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferResponse;
import com.digipals.wms.stocktransfer.service.StockTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-transfers")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService service;

    /**
     * Create Stock Transfer
     */
    @PostMapping
    public StockTransferResponse create(
            @RequestBody CreateStockTransferRequest request) {

        return service.create(request);
    }

    /**
     * Get All Stock Transfers
     */
    @GetMapping
    public List<StockTransferResponse> getAll() {

        return service.findAll();
    }

    /**
     * Get Stock Transfer By Id
     */
    @GetMapping("/{id}")
    public StockTransferResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    /**
     * Approve Stock Transfer
     */
    @PutMapping("/{id}/approve")
    public StockTransferResponse approve(
            @PathVariable UUID id) {

        return service.approve(id);
    }

    /**
     * Issue (Dispatch) Stock Transfer
     */
    @PutMapping("/{id}/issue")
    public StockTransferResponse issue(
            @PathVariable UUID id) {

        return service.issue(id);
    }

    /**
     * Receive Stock Transfer
     */
    @PutMapping("/{id}/receive")
    public StockTransferResponse receive(
            @PathVariable UUID id) {

        return service.receive(id);
    }
}