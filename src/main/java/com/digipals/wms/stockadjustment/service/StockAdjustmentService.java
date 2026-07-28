package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockcount.entity.StockCount;

import java.util.List;
import java.util.UUID;

public interface StockAdjustmentService {

    StockAdjustmentResponse create(
            CreateStockAdjustmentRequest request);

    List<StockAdjustmentResponse> findAll();

    StockAdjustmentResponse findById(
            UUID id);

    StockAdjustmentResponse approve(
            UUID id);

    StockAdjustmentResponse post(
            UUID id);
    
    StockAdjustmentResponse createFromStockCount(
        StockCount stockCount);
}