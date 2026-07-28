package com.digipals.wms.purchaserequisition.validator;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequisitionValidator {

    public void validateDraft(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() !=
                PurchaseRequisitionStatus.DRAFT) {

            throw new RuntimeException(
                    "Only Draft Purchase Requisitions can be modified.");
        }
    }

    public void validateSubmitted(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() !=
                PurchaseRequisitionStatus.SUBMITTED) {

            throw new RuntimeException(
                    "Purchase Requisition must be Submitted.");
        }
    }

    public void validateApproved(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() !=
                PurchaseRequisitionStatus.APPROVED) {

            throw new RuntimeException(
                    "Purchase Requisition must be Approved.");
        }
    }

    public void validateNotConverted(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() ==
                PurchaseRequisitionStatus.CONVERTED_TO_PO) {

            throw new RuntimeException(
                    "Purchase Requisition already converted.");
        }
    }

    public void validateNotCancelled(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() ==
                PurchaseRequisitionStatus.CANCELLED) {

            throw new RuntimeException(
                    "Purchase Requisition is Cancelled.");
        }
    }
}