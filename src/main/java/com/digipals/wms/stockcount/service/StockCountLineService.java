package com.digipals.wms.stockcount.service;

import com.digipals.wms.stockcount.dto.CreateStockCountLineRequest;
import com.digipals.wms.stockcount.dto.StockCountLineResponse;

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
            CreateStockCountLineRequest request);

    void delete(
            UUID id);
}