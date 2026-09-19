package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.TaxPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TaxPostingRepository extends JpaRepository<TaxPosting,UUID> {
 List<TaxPosting> findByCompanyCodeOrderByCreatedAtDesc(String companyCode);
}