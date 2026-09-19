package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.FixedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FixedAssetRepository extends JpaRepository<FixedAsset,UUID>{ Optional<FixedAsset> findByAssetNumber(String assetNumber); List<FixedAsset> findByStatusOrderByAssetNumberAsc(String status); }