package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.MasterInspectionCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MasterInspectionCharacteristicRepository extends JpaRepository<MasterInspectionCharacteristic,UUID>{ Optional<MasterInspectionCharacteristic> findByCodeIgnoreCase(String code); }