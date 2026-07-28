package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentLineRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentLineResponse;

import java.util.List;
import java.util.UUID;

public interface StockAdjustmentLineService {

    StockAdjustmentLineResponse create(
            CreateStockAdjustmentLineRequest request);

    List<StockAdjustmentLineResponse> findAll();

    StockAdjustmentLineResponse findById(
            UUID id);

    List<StockAdjustmentLineResponse> findByAdjustment(
            UUID adjustmentId);

    void delete(
            UUID id);
}