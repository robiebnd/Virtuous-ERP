package com.digipals.wms.outbound.repository;

import com.digipals.wms.outbound.entity.SalesQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalesQuotationRepository extends JpaRepository<SalesQuotation, UUID> {
    Optional<SalesQuotation> findByQuotationNumber(String quotationNumber);
}
