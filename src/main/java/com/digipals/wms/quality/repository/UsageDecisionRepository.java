package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.UsageDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UsageDecisionRepository extends JpaRepository<UsageDecision,UUID>{ Optional<UsageDecision> findByInspectionLotId(UUID lotId); }