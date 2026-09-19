package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.AssetDepreciationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface AssetDepreciationRunRepository extends JpaRepository<AssetDepreciationRun,UUID> {
    boolean existsByAssetIdAndPeriodStart(UUID assetId, LocalDate periodStart);
}