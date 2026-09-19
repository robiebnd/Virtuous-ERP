package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.FiscalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FiscalPeriodRepository extends JpaRepository<FiscalPeriod, UUID> {
    List<FiscalPeriod> findByCompanyCodeAndFiscalYearOrderByPeriodNumber(String companyCode, Integer fiscalYear);
    Optional<FiscalPeriod> findByCompanyCodeAndFiscalYearAndPeriodNumber(String companyCode, Integer fiscalYear, Integer periodNumber);
    Optional<FiscalPeriod> findByCompanyCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String companyCode, LocalDate date1, LocalDate date2);
    long countByCompanyCodeAndFiscalYearAndStatus(String companyCode, Integer fiscalYear, String status);
}