package com.digipals.wms.stocktransfer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockTransferResponse {

    private UUID id;

    private String transferNumber;

    private String sourceWarehouseCode;

    private String sourceWarehouseName;

    private String destinationWarehouseCode;

    private String destinationWarehouseName;

    private String status;

    private String remarks;

    private LocalDateTime transferredAt;

    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    private LocalDateTime issuedAt;

    private LocalDateTime receivedAt;
}