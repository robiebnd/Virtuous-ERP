package com.digipals.wms.bintransfer.service;

import com.digipals.wms.bintransfer.dto.CreateBinTransferRequest;
import com.digipals.wms.bintransfer.dto.BinTransferResponse;

import java.util.List;
import java.util.UUID;

public interface BinTransferService {

    /**
     * Create a new Bin Transfer
     */
    BinTransferResponse create(
            CreateBinTransferRequest request);

    /**
     * Get all Bin Transfers
     */
    List<BinTransferResponse> findAll();

    /**
     * Find Bin Transfer by ID
     */
    BinTransferResponse findById(
            UUID id);

    /**
     * Find Bin Transfers by Warehouse
     */
    List<BinTransferResponse> findByWarehouse(
            UUID warehouseId);

    /**
     * Approve Bin Transfer
     */
    BinTransferResponse approve(
            UUID id);

    /**
     * Post Bin Transfer
     */
    BinTransferResponse post(
            UUID id);

    /**
     * Cancel Bin Transfer
     */
    BinTransferResponse cancel(
            UUID id);

    /**
     * Delete Draft Bin Transfer
     */
    void delete(
            UUID id);
}