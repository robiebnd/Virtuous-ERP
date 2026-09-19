package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.RevenueObligation; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RevenueObligationRepository extends JpaRepository<RevenueObligation,UUID> {
    Optional<RevenueObligation> findByContractId(UUID contractId);
}