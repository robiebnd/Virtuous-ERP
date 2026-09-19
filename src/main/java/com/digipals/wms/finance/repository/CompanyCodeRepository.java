package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.CompanyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CompanyCodeRepository extends JpaRepository<CompanyCode,UUID> {
 Optional<CompanyCode> findByCompanyCodeIgnoreCase(String companyCode);
 List<CompanyCode> findByActiveTrueOrderByCompanyCode();
}