package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;

public final class PurchaseRequisitionMapper {

    private PurchaseRequisitionMapper() {
    }

    public static PurchaseRequisitionResponse toResponse(PurchaseRequisition requisition) {
        if (requisition == null) {
            return null;
        }

        return PurchaseRequisitionResponse.builder()
                .id(requisition.getId())
                .requisitionNumber(requisition.getRequisitionNumber())
                .warehouseCode(requisition.getWarehouse() != null
                        ? requisition.getWarehouse().getCode()
                        : null)
                .warehouseName(requisition.getWarehouse() != null
                        ? requisition.getWarehouse().getName()
                        : null)
                .status(requisition.getStatus())
                .department(requisition.getDepartment())
                .remarks(requisition.getRemarks())
                .rejectionReason(requisition.getRejectionReason())
                .requestedById(requisition.getRequestedBy() != null
                        ? requisition.getRequestedBy().getId()
                        : null)
                .approvedById(requisition.getApprovedBy() != null
                        ? requisition.getApprovedBy().getId()
                        : null)
                .rejectedById(requisition.getRejectedBy() != null
                        ? requisition.getRejectedBy().getId()
                        : null)
                .cancelledById(requisition.getCancelledBy() != null
                        ? requisition.getCancelledBy().getId()
                        : null)
                .submittedAt(requisition.getSubmittedAt())
                .approvedAt(requisition.getApprovedAt())
                .rejectedAt(requisition.getRejectedAt())
                .cancelledAt(requisition.getCancelledAt())
                .createdAt(requisition.getCreatedAt())
                .build();
    }
}
