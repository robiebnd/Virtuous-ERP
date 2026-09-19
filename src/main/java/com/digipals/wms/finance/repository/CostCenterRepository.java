package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CostCenterRepository extends JpaRepository<CostCenter,UUID>{ Optional<CostCenter> findByCode(String code); List<CostCenter> findByActiveTrueOrderByCodeAsc(); }