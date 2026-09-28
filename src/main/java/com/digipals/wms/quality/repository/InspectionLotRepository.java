package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.InspectionLot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface InspectionLotRepository extends JpaRepository<InspectionLot,UUID>{ Optional<InspectionLot> findByLotNumber(String number); List<InspectionLot> findByStatusOrderByCreatedAtAsc(String status); }