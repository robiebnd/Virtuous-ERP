package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.CollectionCase; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CollectionCaseRepository extends JpaRepository<CollectionCase,UUID> {
    List<CollectionCase> findByStatusOrderByDueDateAsc(String status);
}