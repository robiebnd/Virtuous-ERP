package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;

public final class PurchaseRequisitionMapper {

    private PurchaseRequisitionMapper() {
    }

    public static PurchaseRequisitionResponse toResponse(
            PurchaseRequisition requisition) {

        return PurchaseRequisitionResponse.builder()

                .id(
                        requisition.getId())

                .requisitionNumber(
                        requisition.getRequisitionNumber())

                .warehouseCode(
                        requisition.getWarehouse().getCode())

                .warehouseName(
                        requisition.getWarehouse().getName())

                .status(
                        requisition.getStatus())

                .department(
                        requisition.getDepartment())

                .remarks(
                        requisition.getRemarks())

                .approvedAt(
                        requisition.getApprovedAt())

                .createdAt(
                        requisition.getCreatedAt())

                .build();
    }
}