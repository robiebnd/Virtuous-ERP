package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.MaterialLedgerPeriod; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MaterialLedgerPeriodRepository extends JpaRepository<MaterialLedgerPeriod,UUID> {
    Optional<MaterialLedgerPeriod> findByProductCodeAndPeriodStart(String productCode,java.time.LocalDate periodStart);
}