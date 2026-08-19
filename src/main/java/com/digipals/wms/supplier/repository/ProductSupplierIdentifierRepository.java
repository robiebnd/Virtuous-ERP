package com.digipals.wms.supplier.repository;

import com.digipals.wms.supplier.entity.ProductSupplierIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductSupplierIdentifierRepository
        extends JpaRepository<ProductSupplierIdentifier, UUID> {

    Optional<ProductSupplierIdentifier> findBySupplierIdAndSupplierItemCodeIgnoreCase(
            UUID supplierId,
            String supplierItemCode);

    boolean existsBySupplierIdAndSupplierItemCodeIgnoreCase(
            UUID supplierId,
            String supplierItemCode);
}
