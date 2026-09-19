package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.InternalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface InternalOrderRepository extends JpaRepository<InternalOrder, UUID> {
    Optional<InternalOrder> findByOrderCode(String orderCode);
    List<InternalOrder> findByStatusOrderByOrderCodeAsc(String status);
}