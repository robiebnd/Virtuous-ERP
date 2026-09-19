package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.ProductCostEstimate; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ProductCostEstimateRepository extends JpaRepository<ProductCostEstimate,UUID> {
    List<ProductCostEstimate> findByProductCodeOrderByCostingDateDesc(String productCode);
}