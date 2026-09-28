package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.InspectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface InspectionResultRepository extends JpaRepository<InspectionResult,UUID>{ List<InspectionResult> findByInspectionLotId(UUID lotId); }