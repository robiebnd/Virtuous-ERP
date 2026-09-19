package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.FinanceDispute; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface FinanceDisputeRepository extends JpaRepository<FinanceDispute,UUID> {
    List<FinanceDispute> findByStatusOrderByCreatedAtDesc(String status);
}