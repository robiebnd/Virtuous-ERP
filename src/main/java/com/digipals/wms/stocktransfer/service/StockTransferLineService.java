package com.digipals.wms.stocktransfer.service;

import com.digipals.wms.stocktransfer.dto.CreateStockTransferLineRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferLineResponse;

import java.util.List;
import java.util.UUID;

public interface StockTransferLineService {

    StockTransferLineResponse create(CreateStockTransferLineRequest request);

    List<StockTransferLineResponse> findAll();

    StockTransferLineResponse findById(UUID id);

    // CHANGED: Matches implementation and controller name
    List<StockTransferLineResponse> findByStockTransferId(UUID stockTransferId);

    void delete(UUID id);
}