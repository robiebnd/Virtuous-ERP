package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;

public final class PurchaseRequisitionLineMapper {

    private PurchaseRequisitionLineMapper() {
    }

    public static PurchaseRequisitionLineResponse toResponse(PurchaseRequisitionLine line) {
        if (line == null) {
            return null;
        }

        var product = line.getProduct();
        var pir = line.getPurchasingInfoRecord();
        var supplierProduct = pir == null ? null : pir.getSupplierProduct();
        var supplier = line.getSourceSupplier();

        return PurchaseRequisitionLineResponse.builder()
                .id(line.getId())
                .productId(product == null ? null : product.getId())
                .sku(product == null ? null : product.getSku())
                .productName(product == null ? null : product.getName())
                .quantity(line.getQuantity())
                .estimatedUnitCost(line.getEstimatedUnitCost())
                .sourceSupplierId(supplier == null ? null : supplier.getId())
                .sourceSupplierCode(supplier == null ? null : supplier.getCode())
                .sourceSupplierName(supplier == null ? null : supplier.getName())
                .purchasingInfoRecordId(pir == null ? null : pir.getId())
                .plannedDeliveryDays(pir == null ? null : pir.getPlannedDeliveryDays())
                .supplierItemCode(supplierProduct == null ? null : supplierProduct.getSupplierItemCode())
                .remarks(line.getRemarks())
                .build();
    }
}