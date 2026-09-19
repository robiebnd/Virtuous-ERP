package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.RevenueContract; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RevenueContractRepository extends JpaRepository<RevenueContract,UUID> {
    Optional<RevenueContract> findByContractNumber(String contractNumber);
}