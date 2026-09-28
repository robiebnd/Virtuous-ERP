package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.QualityInfoRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; import java.util.UUID;
public interface QualityInfoRecordRepository extends JpaRepository<QualityInfoRecord,UUID>{Optional<QualityInfoRecord> findBySupplierIdAndProductId(UUID supplierId,UUID productId);}