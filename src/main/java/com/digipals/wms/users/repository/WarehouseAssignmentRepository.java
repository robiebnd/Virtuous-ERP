package com.digipals.wms.users.repository;

import com.digipals.wms.users.entity.WarehouseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WarehouseAssignmentRepository
        extends JpaRepository<WarehouseAssignment, UUID> {

    List<WarehouseAssignment> findByUserId(
            UUID userId);

    List<WarehouseAssignment> findByWarehouseId(
            UUID warehouseId);

    boolean existsByUserIdAndWarehouseId(
            UUID userId,
            UUID warehouseId);

    void deleteByUserIdAndWarehouseId(
            UUID userId,
            UUID warehouseId);

    long countByUserId(
            UUID userId);
}