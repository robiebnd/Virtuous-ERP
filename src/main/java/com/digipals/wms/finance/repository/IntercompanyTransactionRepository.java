package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.IntercompanyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface IntercompanyTransactionRepository extends JpaRepository<IntercompanyTransaction,UUID> {
 List<IntercompanyTransaction> findByStatusOrderByTransactionDateDesc(String status);
 List<IntercompanyTransaction> findBySourceCompanyCodeAndTargetCompanyCodeAndStatus(String source,String target,String status);
}