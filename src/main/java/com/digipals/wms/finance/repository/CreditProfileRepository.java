package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.CreditProfile; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CreditProfileRepository extends JpaRepository<CreditProfile,UUID> {
    Optional<CreditProfile> findByCustomerId(UUID customerId);
}