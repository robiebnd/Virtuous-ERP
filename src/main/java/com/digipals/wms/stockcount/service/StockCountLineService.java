package com.digipals.wms.stockcount.service;

import com.digipals.wms.stockcount.dto.CreateStockCountLineRequest;
import com.digipals.wms.stockcount.dto.StockCountLineResponse;
import com.digipals.wms.stockcount.dto.UpdateStockCountLineRequest;

import java.util.List;
import java.util.UUID;

public interface StockCountLineService {

    StockCountLineResponse create(
            CreateStockCountLineRequest request);

    List<StockCountLineResponse> findAll();

    StockCountLineResponse findById(
            UUID id);

    List<StockCountLineResponse> findByStockCount(
            UUID stockCountId);

    StockCountLineResponse updateCount(
            UUID lineId,
            UpdateStockCountLineRequest request);

    void delete(
            UUID id);
}
