package com.digipals.wms.supplier.repository;

import com.digipals.wms.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository
        extends JpaRepository<Supplier, UUID> {

    boolean existsByCode(String code);

    Optional<Supplier> findByCode(String code);

    List<Supplier> findByActiveTrue();
}