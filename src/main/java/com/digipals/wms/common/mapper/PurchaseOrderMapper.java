package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public final class PurchaseOrderMapper {
    private PurchaseOrderMapper() {}
    public static PurchaseOrderResponse toResponse(PurchaseOrder p) {
        if (p == null) return null;
        List<PurchaseOrderLineResponse> lines = p.getLines() == null ? Collections.emptyList() : p.getLines().stream().map(PurchaseOrderMapper::toLineResponse).toList();
        return PurchaseOrderResponse.builder().id(p.getId()).poNumber(p.getPoNumber()).status(p.getStatus()).source(p.getSource()).currency(p.getCurrency())
                .documentType(p.getDocumentType()).purchasingOrganization(p.getPurchasingOrganization()).purchasingGroup(p.getPurchasingGroup()).companyCode(p.getCompanyCode())
                .paymentTerms(p.getPaymentTerms()).incoterms(p.getIncoterms()).confirmationControl(p.getConfirmationControl()).outputStatus(p.getOutputStatus())
                .supplierId(p.getSupplier()==null?null:p.getSupplier().getId()).supplierCode(p.getSupplier()==null?null:p.getSupplier().getCode()).supplierName(p.getSupplier()==null?null:p.getSupplier().getName())
                .warehouseId(p.getWarehouse()==null?null:p.getWarehouse().getId()).warehouseCode(p.getWarehouse()==null?null:p.getWarehouse().getCode()).warehouseName(p.getWarehouse()==null?null:p.getWarehouse().getName())
                .purchaseRequisitionId(p.getPurchaseRequisition()==null?null:p.getPurchaseRequisition().getId()).purchaseRequisitionNumber(p.getPurchaseRequisition()==null?null:p.getPurchaseRequisition().getRequisitionNumber())
                .createdById(p.getCreatedBy()==null?null:p.getCreatedBy().getId()).createdBy(p.getCreatedBy()==null?null:p.getCreatedBy().getUsername())
                .approvedById(p.getApprovedBy()==null?null:p.getApprovedBy().getId()).approvedBy(p.getApprovedBy()==null?null:p.getApprovedBy().getUsername()).approvedAt(p.getApprovedAt())
                .active(p.getActive()).createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt()).lines(lines).build();
    }
    public static PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine l) {
        if (l == null) return null;
        return PurchaseOrderLineResponse.builder().id(l.getId()).purchaseRequisitionLineId(l.getPurchaseRequisitionLine()==null?null:l.getPurchaseRequisitionLine().getId())
                .productId(l.getProduct()==null?null:l.getProduct().getId()).sku(l.getProduct()==null?null:l.getProduct().getSku()).productName(l.getProduct()==null?null:l.getProduct().getName())
                .quantity(scale(l.getQuantity())).receivedQuantity(scale(l.getReceivedQuantity())).outstandingQuantity(scale(l.getOutstandingQuantity())).unitPrice(scale(l.getUnitPrice())).lineTotal(scale(l.getLineTotal())).build();
    }
    private static BigDecimal scale(BigDecimal v) { return v == null ? null : v.setScale(2, RoundingMode.HALF_UP); }
}