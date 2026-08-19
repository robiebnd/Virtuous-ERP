package com.digipals.wms.productsupplieridentifier.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.productsupplieridentifier.dto.SupplierProductLookupResponse;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.productsupplieridentifier.repository.ProductSupplierIdentifierRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierProductMatchingService {

    private final ProductSupplierIdentifierRepository identifierRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public SupplierProductLookupResponse resolve(String supplierCode, String supplierItemCode) {
        Supplier supplier = supplierRepository.findByCode(supplierCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierCode));

        ProductSupplierIdentifier identifier = identifierRepository
                .findBySupplierIdAndSupplierItemCodeIgnoreCase(supplier.getId(), supplierItemCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No product mapping found for supplier " + supplier.getCode()
                                + " and supplier item code " + supplierItemCode));

        Product product = identifier.getProduct();
        return SupplierProductLookupResponse.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .supplierId(supplier.getId())
                .supplierCode(supplier.getCode())
                .supplierName(supplier.getName())
                .supplierItemCode(identifier.getSupplierItemCode())
                .supplierItemName(identifier.getSupplierItemName())
                .build();
    }
}
