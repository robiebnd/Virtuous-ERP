package com.digipals.wms.common.mapper;

import com.digipals.wms.stocktransfer.dto.StockTransferResponse;
import com.digipals.wms.stocktransfer.entity.StockTransfer;

public final class StockTransferMapper {

    private StockTransferMapper() {
    }

    public static StockTransferResponse toResponse(
            StockTransfer transfer) {

        return StockTransferResponse.builder()

                .id(
                        transfer.getId())

                .transferNumber(
                        transfer.getTransferNumber())

                .sourceWarehouseCode(
                        transfer.getSourceWarehouse().getCode())

                .sourceWarehouseName(
                        transfer.getSourceWarehouse().getName())

                .destinationWarehouseCode(
                        transfer.getDestinationWarehouse().getCode())

                .destinationWarehouseName(
                        transfer.getDestinationWarehouse().getName())

                .status(
                        transfer.getStatus().name())

                .remarks(
                        transfer.getRemarks())

                .transferredAt(
                        transfer.getTransferredAt())

                .createdAt(
                        transfer.getCreatedAt())

                .approvedAt(
                        transfer.getApprovedAt())

                .issuedAt(
                        transfer.getIssuedAt())

                .receivedAt(
                        transfer.getReceivedAt())

                .build();
    }
}