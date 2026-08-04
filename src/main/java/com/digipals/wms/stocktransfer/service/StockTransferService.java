package com.digipals.wms.stocktransfer.service;

import com.digipals.wms.stocktransfer.dto.CreateStockTransferRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferResponse;

import java.util.List;
import java.util.UUID;

public interface StockTransferService {

    /**
     * Create a Stock Transfer.
     */
    StockTransferResponse create(
            CreateStockTransferRequest request);

    /**
     * Approve a Draft Stock Transfer.
     */
    StockTransferResponse approve(
            UUID transferId);

    /**
     * Issue stock from the source warehouse.
     */
    StockTransferResponse issue(
            UUID transferId);

    /**
     * Receive stock into the destination warehouse.
     */
    StockTransferResponse receive(
            UUID transferId);

    /**
     * Cancel a Stock Transfer.
     */
    StockTransferResponse cancel(
            UUID transferId);

    /**
     * Delete a Draft Stock Transfer.
     */
    void delete(
            UUID transferId);

    /**
     * Find all Stock Transfers.
     */
    List<StockTransferResponse> findAll();

    /**
     * Find a Stock Transfer by ID.
     */
    StockTransferResponse findById(
            UUID transferId);
}