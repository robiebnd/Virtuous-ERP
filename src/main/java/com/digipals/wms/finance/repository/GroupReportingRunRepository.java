package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.GroupReportingRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface GroupReportingRunRepository extends JpaRepository<GroupReportingRun,UUID> {
 Optional<GroupReportingRun> findByGroupIdAndFiscalYearAndPeriodNumber(UUID groupId,Integer year,Integer period);
 List<GroupReportingRun> findByGroupIdOrderByFiscalYearDescPeriodNumberDesc(UUID groupId);
}