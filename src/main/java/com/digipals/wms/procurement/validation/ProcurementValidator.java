package com.digipals.wms.procurement.validation;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import org.springframework.stereotype.Component;

@Component
public class ProcurementValidator {

    public void validateApproved(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() !=
                PurchaseRequisitionStatus.APPROVED) {

            throw new InvalidWorkflowException(
                    "Only approved Purchase Requisitions can generate Purchase Orders.");
        }
    }

    public void validateNotConverted(
            PurchaseRequisition requisition) {

        if (requisition.getStatus() ==
                PurchaseRequisitionStatus.CONVERTED_TO_PO) {

            throw new InvalidWorkflowException(
                    "Purchase Requisition has already been converted.");
        }
    }
}
