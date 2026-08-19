package com.digipals.wms.procurement.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

/**
 * Resolves supplier quotation item identifiers to authoritative ERP products.
 * Supplier item codes are never treated as ERP SKUs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierProductResolutionService {

    private final ProductSupplierIdentifierRepository identifierRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public Product resolve(UUID supplierId, String supplierItemCode) {
        if (supplierId == null) {
            throw new ResourceNotFoundException("Supplier is required to resolve a supplier item code.");
        }
        if (supplierItemCode == null || supplierItemCode.isBlank()) {
            throw new ResourceNotFoundException("Supplier item code is required to resolve the ERP product.");
        }

        return identifierRepository.findBySupplierIdAndSupplierItemCodeIgnoreCase(
                        supplierId, supplierItemCode.trim())
                .map(ProductSupplierIdentifier::getProduct)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No ERP product mapping found for supplier item code '" + supplierItemCode.trim() + "'."));
    }

    public Product resolveOrCreateMapping(UUID supplierId, String supplierItemCode, String supplierItemName) {
        if (supplierId == null) {
            throw new ResourceNotFoundException("Supplier is required to resolve a supplier item code.");
        }
        if (supplierItemCode == null || supplierItemCode.isBlank()) {
            throw new ResourceNotFoundException("Supplier item code is required.");
        }

        String code = supplierItemCode.trim();
        var existing = identifierRepository.findBySupplierIdAndSupplierItemCodeIgnoreCase(supplierId, code);
        if (existing.isPresent()) {
            return existing.get().getProduct();
        }

        // Safe fallback: only accept an exact ERP SKU match. Supplier codes must not
        // silently create a new product or be assumed to equal the ERP SKU.
        Product product = productRepository.findBySkuIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No ERP product mapping found for supplier item code '" + code
                                + "'. Create a supplier-product mapping before processing the quotation."));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        identifierRepository.save(ProductSupplierIdentifier.builder()
                .supplier(supplier)
                .product(product)
                .supplierItemCode(code)
                .supplierItemName(supplierItemName == null ? null : supplierItemName.trim())
                .build());

        return product;
    }
}
