package com.digipals.wms.stockcount.service;

import com.digipals.wms.stockcount.dto.CreateStockCountRequest;
import com.digipals.wms.stockcount.dto.StockCountResponse;

import java.util.List;
import java.util.UUID;

public interface StockCountService {

    StockCountResponse create(
            CreateStockCountRequest request);

    List<StockCountResponse> findAll();

    StockCountResponse findById(
            UUID id);

    StockCountResponse loadInventory(
            UUID id);

    StockCountResponse complete(
            UUID id);

    StockCountResponse generateAdjustment(
            UUID id);

    void delete(
            UUID id);
}