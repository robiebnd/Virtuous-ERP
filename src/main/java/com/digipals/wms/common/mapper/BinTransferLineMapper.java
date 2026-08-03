package com.digipals.wms.common.mapper;

import com.digipals.wms.bintransfer.dto.BinTransferLineResponse;
import com.digipals.wms.bintransfer.entity.BinTransferLine;

public class BinTransferLineMapper {

    public static BinTransferLineResponse toResponse(
            BinTransferLine entity) {

        if (entity == null) {
            return null;
        }

        return BinTransferLineResponse.builder()

                .id(
                        entity.getId())

                .binTransferId(
                        entity.getBinTransfer() != null
                                ? entity.getBinTransfer().getId()
                                : null)

                .productId(
                        entity.getProduct() != null
                                ? entity.getProduct().getId()
                                : null)

                .sku(
                        entity.getProduct() != null
                                ? entity.getProduct().getSku()
                                : null)

                .productName(
                        entity.getProduct() != null
                                ? entity.getProduct().getName()
                                : null)

                .quantity(
                        entity.getQuantity())

                .remarks(
                        entity.getRemarks())

                .createdAt(
                        entity.getCreatedAt())

                .updatedAt(
                        entity.getUpdatedAt())

                .build();
    }
}