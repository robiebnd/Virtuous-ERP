package com.digipals.wms.outbound.repository;
import com.digipals.wms.outbound.entity.OutboundDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OutboundDeliveryRepository extends JpaRepository<OutboundDelivery, UUID> {
    Optional<OutboundDelivery> findByDeliveryNumber(String deliveryNumber);
    List<OutboundDelivery> findBySalesOrderId(UUID salesOrderId);
}
