package com.digipals.wms.uom.repository;

import com.digipals.wms.uom.entity.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitOfMeasureRepository
        extends JpaRepository<UnitOfMeasure, UUID> {

    boolean existsByCode(String code);

    Optional<UnitOfMeasure> findByCode(String code);

    List<UnitOfMeasure> findByActiveTrue();
}