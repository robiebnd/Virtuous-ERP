package com.digipals.wms.manufacturing.repository;

import com.digipals.wms.manufacturing.entity.MrpComponentRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MrpComponentRequirementRepository extends JpaRepository<MrpComponentRequirement, UUID> {
    List<MrpComponentRequirement> findByMrpPlanIdOrderByBomLevelAscIdAsc(UUID mrpPlanId);
}