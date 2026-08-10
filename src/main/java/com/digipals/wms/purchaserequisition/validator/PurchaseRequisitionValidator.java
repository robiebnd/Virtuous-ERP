package com.digipals.wms.purchaserequisition.validator;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequisitionValidator {

    public void validateDraft(PurchaseRequisition requisition) {
        requireStatus(
                requisition,
                PurchaseRequisitionStatus.DRAFT,
                "Only Draft Purchase Requisitions can be modified.");
    }

    public void validateSubmitted(PurchaseRequisition requisition) {
        requireStatus(
                requisition,
                PurchaseRequisitionStatus.SUBMITTED,
                "Purchase Requisition must be Submitted.");
    }

    public void validateApproved(PurchaseRequisition requisition) {
        requireStatus(
                requisition,
                PurchaseRequisitionStatus.APPROVED,
                "Purchase Requisition must be Approved.");
    }

    public void validateNotConverted(PurchaseRequisition requisition) {
        if (requisition.getStatus() == PurchaseRequisitionStatus.CONVERTED_TO_PO) {
            throw new RuntimeException(
                    "Purchase Requisition already converted to a Purchase Order.");
        }
    }

    public void validateNotCancelled(PurchaseRequisition requisition) {
        if (requisition.getStatus() == PurchaseRequisitionStatus.CANCELLED) {
            throw new RuntimeException("Purchase Requisition is Cancelled.");
        }
    }

    public void validateCanCancel(PurchaseRequisition requisition) {
        PurchaseRequisitionStatus status = requisition.getStatus();

        if (status == PurchaseRequisitionStatus.CANCELLED) {
            throw new RuntimeException("Purchase Requisition is already Cancelled.");
        }

        if (status == PurchaseRequisitionStatus.CONVERTED_TO_PO) {
            throw new RuntimeException(
                    "Converted Purchase Requisitions cannot be cancelled.");
        }
    }

    private void requireStatus(
            PurchaseRequisition requisition,
            PurchaseRequisitionStatus expected,
            String message) {

        if (requisition == null || requisition.getStatus() != expected) {
            throw new RuntimeException(message);
        }
    }
}
