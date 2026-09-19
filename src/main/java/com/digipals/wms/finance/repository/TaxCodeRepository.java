package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.TaxCode; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TaxCodeRepository extends JpaRepository<TaxCode,UUID> {
    Optional<TaxCode> findByTaxCode(String taxCode);
}