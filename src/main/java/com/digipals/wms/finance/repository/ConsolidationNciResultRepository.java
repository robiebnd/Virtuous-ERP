package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.ConsolidationNciResult; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ConsolidationNciResultRepository extends JpaRepository<ConsolidationNciResult,UUID> { List<ConsolidationNciResult> findByRunIdOrderByNciAmountDesc(UUID runId); }