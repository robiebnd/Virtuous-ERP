package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.GroupReportingBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface GroupReportingBalanceRepository extends JpaRepository<GroupReportingBalance,UUID> {
 List<GroupReportingBalance> findByRunIdOrderByAccountCodeAscCompanyCodeAsc(UUID runId);
}