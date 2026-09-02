package com.digipals.wms.outbound.repository;
import com.digipals.wms.outbound.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    List<SalesOrder> findByCustomerId(UUID customerId);
}
