package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.AccrualTemplate; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AccrualTemplateRepository extends JpaRepository<AccrualTemplate,UUID> {
    Optional<AccrualTemplate> findByTemplateCode(String templateCode);
}