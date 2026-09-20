package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.TaxPosting;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;
public interface TaxPostingRepository extends JpaRepository<TaxPosting,UUID> {
 List<TaxPosting> findByCompanyCodeOrderByCreatedAtDesc(String companyCode);
 @Query("select p from TaxPosting p where p.companyCode=:companyCode and p.accountingDocument.postingDate>=:startDate and p.accountingDocument.postingDate<:endDate order by p.accountingDocument.postingDate desc,p.createdAt desc")
 List<TaxPosting> findByCompanyCodeAndPostingDateRange(@Param("companyCode") String companyCode,@Param("startDate") LocalDateTime startDate,@Param("endDate") LocalDateTime endDate);
}
