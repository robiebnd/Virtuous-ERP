package com.digipals.wms.salesorder.repository;

import com.digipals.wms.salesorder.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    Optional<SalesOrder> findBySapOrderNumber(String sapOrderNumber);

    List<SalesOrder> findByCustomerCodeOrderByOrderDateDesc(String customerCode);
}
