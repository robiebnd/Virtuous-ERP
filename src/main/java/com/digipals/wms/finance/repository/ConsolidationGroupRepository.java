package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.ConsolidationGroup; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ConsolidationGroupRepository extends JpaRepository<ConsolidationGroup,UUID> {
    Optional<ConsolidationGroup> findByGroupCode(String groupCode);
}