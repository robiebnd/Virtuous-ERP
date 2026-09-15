package com.digipals.wms.supplier.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.productsupplieridentifier.repository.ProductSupplierIdentifierRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductSupplierIdentifierService {

    private final ProductSupplierIdentifierRepository repository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public ProductSupplierIdentifier create(
            UUID supplierId,
            UUID productId,
            String supplierItemCode,
            String supplierItemName) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        String normalizedCode = normalize(supplierItemCode);
        if (normalizedCode == null) {
            throw new IllegalArgumentException("Supplier item code is required.");
        }

        if (repository.existsBySupplierIdAndSupplierItemCodeIgnoreCase(supplierId, normalizedCode)) {
            throw new DuplicateResourceException(
                    "Supplier item code is already mapped for this supplier: " + normalizedCode);
        }

        return repository.save(ProductSupplierIdentifier.builder()
                .supplier(supplier)
                .product(product)
                .supplierItemCode(normalizedCode)
                .supplierItemName(normalize(supplierItemName))
                .build());
    }

    @Transactional(readOnly = true)
    public Product resolveProduct(UUID supplierId, String supplierItemCode) {
        String normalizedCode = normalize(supplierItemCode);
        if (normalizedCode == null) {
            throw new ResourceNotFoundException("Supplier item code is required to resolve the product.");
        }

        return repository.findBySupplierIdAndSupplierItemCodeIgnoreCase(supplierId, normalizedCode)
                .map(ProductSupplierIdentifier::getProduct)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No product mapping found for supplier item code '"
                                + normalizedCode + "'."));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
