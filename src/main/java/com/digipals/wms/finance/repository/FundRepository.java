package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.Fund; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface FundRepository extends JpaRepository<Fund,UUID> {
    Optional<Fund> findByFundCode(String fundCode);
}