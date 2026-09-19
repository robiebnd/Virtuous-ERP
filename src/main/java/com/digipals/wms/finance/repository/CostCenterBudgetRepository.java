package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.CostCenterBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CostCenterBudgetRepository extends JpaRepository<CostCenterBudget, UUID> {
    Optional<CostCenterBudget> findByCostCenterCodeAndFiscalYear(String costCenterCode, Integer fiscalYear);
    List<CostCenterBudget> findByFiscalYearOrderByCostCenterCodeAsc(Integer fiscalYear);
}