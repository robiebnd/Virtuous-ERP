package com.digipals.wms.dunning.repository;

import com.digipals.wms.dunning.entity.DunningCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DunningCaseRepository extends JpaRepository<DunningCase, UUID> {
    Optional<DunningCase> findByDunningNumber(String dunningNumber);
    Optional<DunningCase> findByBillingDocumentIdAndStatusNot(UUID billingDocumentId, com.digipals.wms.dunning.entity.DunningStatus status);
    List<DunningCase> findByCustomerCodeOrderByDunningDateDesc(String customerCode);
}
