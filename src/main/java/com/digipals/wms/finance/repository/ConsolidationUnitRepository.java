package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.ConsolidationUnit; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ConsolidationUnitRepository extends JpaRepository<ConsolidationUnit,UUID> {
    Optional<ConsolidationUnit> findByUnitCode(String unitCode);
}