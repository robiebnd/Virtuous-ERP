package com.digipals.wms.outbounddelivery.repository;

import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboundDeliveryRepository extends JpaRepository<OutboundDelivery, UUID> {
    Optional<OutboundDelivery> findByDeliveryNumber(String deliveryNumber);
    List<OutboundDelivery> findBySalesOrderIdOrderByCreatedAtDesc(UUID salesOrderId);
    boolean existsBySalesOrderId(UUID salesOrderId);
}
