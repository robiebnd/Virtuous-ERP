package com.digipals.wms.procurement.service;

import com.digipals.wms.products.Product;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.supplier.entity.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Converts an extracted supplier quotation line into a PR line using the
 * authoritative ERP Product resolved through the supplier-product mapping.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuotationRequisitionLineResolver {

    private final SupplierProductResolutionService productResolutionService;

    public PurchaseRequisitionLine resolve(
            Supplier supplier,
            String supplierItemCode,
            String supplierItemName,
            BigDecimal quantity,
            BigDecimal quotedUnitPrice,
            String remarks) {

        Product product = productResolutionService.resolveOrCreateMapping(
                supplier.getId(), supplierItemCode, supplierItemName);

        return PurchaseRequisitionLine.builder()
                .product(product)
                .quantity(quantity)
                .estimatedUnitCost(quotedUnitPrice)
                .remarks(remarks)
                .build();
    }
}
