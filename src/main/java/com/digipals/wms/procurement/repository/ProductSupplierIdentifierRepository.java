package com.digipals.wms.procurement.repository;

import com.digipals.wms.procurement.entity.ProductSupplierIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductSupplierIdentifierRepository
        extends JpaRepository<ProductSupplierIdentifier, UUID> {

    Optional<ProductSupplierIdentifier> findBySupplierIdAndSupplierItemCodeIgnoreCase(
            UUID supplierId,
            String supplierItemCode);
}
