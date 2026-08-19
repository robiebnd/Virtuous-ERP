package com.digipals.wms.productsupplieridentifier.repository;

import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductSupplierIdentifierRepository
        extends JpaRepository<ProductSupplierIdentifier, UUID> {

    Optional<ProductSupplierIdentifier> findBySupplierIdAndSupplierItemCodeIgnoreCase(
            UUID supplierId,
            String supplierItemCode);

    List<ProductSupplierIdentifier> findByProductId(UUID productId);

    List<ProductSupplierIdentifier> findBySupplierId(UUID supplierId);

    boolean existsBySupplierIdAndSupplierItemCodeIgnoreCase(
            UUID supplierId,
            String supplierItemCode);

    boolean existsBySupplierIdAndSupplierItemCodeIgnoreCaseAndIdNot(
            UUID supplierId,
            String supplierItemCode,
            UUID id);
}
