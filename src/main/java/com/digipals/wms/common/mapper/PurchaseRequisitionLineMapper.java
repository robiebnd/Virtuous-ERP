package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;


public final class PurchaseRequisitionLineMapper {

    private PurchaseRequisitionLineMapper() {
    }

    public static PurchaseRequisitionLineResponse toResponse(
            PurchaseRequisitionLine line) {

        if (line == null) {
            return null;
        }

        return PurchaseRequisitionLineResponse.builder()

                .id(line.getId())

                .productId(line.getProduct().getId())

                .sku(line.getProduct().getSku())

                .productName(line.getProduct().getName())

                .quantity(line.getQuantity())

                .estimatedUnitCost(
                        line.getEstimatedUnitCost())

                .remarks(
                        line.getRemarks())

                .build();
    }
}