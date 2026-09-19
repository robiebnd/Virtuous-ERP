package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.ProfitabilitySegment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProfitabilitySegmentRepository extends JpaRepository<ProfitabilitySegment,UUID> {
 Optional<ProfitabilitySegment> findBySegmentCodeIgnoreCase(String code);
 List<ProfitabilitySegment> findByCompanyCodeOrderBySegmentCode(String companyCode);
}