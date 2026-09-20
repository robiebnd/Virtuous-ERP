package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.GroupAccountMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface GroupAccountMappingRepository extends JpaRepository<GroupAccountMapping, UUID> {
    List<GroupAccountMapping> findByGroupIdAndActiveTrueOrderByCompanyCodeAscLocalAccountCodeAsc(UUID groupId);
    Optional<GroupAccountMapping> findByGroupIdAndCompanyCodeAndLocalAccountCode(UUID groupId,String companyCode,String localAccountCode);
}
