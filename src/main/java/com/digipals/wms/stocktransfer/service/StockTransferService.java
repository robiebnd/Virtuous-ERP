package com.digipals.wms.stocktransfer.service;


import com.digipals.wms.stocktransfer.dto.CreateStockTransferRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferResponse;
import java.util.List;
import java.util.UUID;

public interface StockTransferService {

    StockTransferResponse create(
            CreateStockTransferRequest request);

    List<StockTransferResponse> findAll();

    StockTransferResponse findById(
            UUID id);

    StockTransferResponse approve(
            UUID id);

    StockTransferResponse issue(
            UUID id);

    StockTransferResponse receive(
            UUID id);
}