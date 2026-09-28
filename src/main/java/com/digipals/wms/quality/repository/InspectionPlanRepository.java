package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.InspectionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface InspectionPlanRepository extends JpaRepository<InspectionPlan,UUID>{ Optional<InspectionPlan> findByPlanNumber(String number); Optional<InspectionPlan> findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(UUID productId,String plantCode,String status); }