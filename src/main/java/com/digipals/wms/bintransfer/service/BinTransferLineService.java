package com.digipals.wms.bintransfer.service;

import com.digipals.wms.bintransfer.dto.BinTransferLineResponse;
import com.digipals.wms.bintransfer.dto.CreateBinTransferLineRequest;

import java.util.List;
import java.util.UUID;

public interface BinTransferLineService {

    /**
     * Add Line
     */
    BinTransferLineResponse create(
            CreateBinTransferLineRequest request);

    /**
     * Find all lines
     */
    List<BinTransferLineResponse> findAll();

    /**
     * Find line by ID
     */
    BinTransferLineResponse findById(
            UUID id);

    /**
     * Find all lines for a Bin Transfer
     */
    List<BinTransferLineResponse> findByBinTransferId(
            UUID transferId);

    /**
     * Update quantity/remarks
     * (Allowed only while transfer is DRAFT)
     */
    BinTransferLineResponse update(
            UUID id,
            CreateBinTransferLineRequest request);

    /**
     * Delete line
     * (Allowed only while transfer is DRAFT)
     */
    void delete(
            UUID id);
}