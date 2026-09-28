package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.QualityCatalogCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface QualityCatalogCodeRepository extends JpaRepository<QualityCatalogCode,UUID>{List<QualityCatalogCode> findByCatalogTypeIgnoreCase(String type);}