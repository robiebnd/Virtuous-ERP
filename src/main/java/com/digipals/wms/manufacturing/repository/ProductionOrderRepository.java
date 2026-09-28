package com.digipals.wms.manufacturing.repository;

import com.digipals.wms.manufacturing.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder,UUID> {
 Optional<ProductionOrder> findByOrderNumber(String n);
 List<ProductionOrder> findByStatus(String status);
 List<ProductionOrder> findByProductIdAndPlantCodeAndStatusIn(UUID productId,String plantCode,Collection<String> statuses);
}