package com.digipals.wms.goodsmovement.repository;

import com.digipals.wms.goodsmovement.entity.GoodsMovement;
import com.digipals.wms.goodsmovement.entity.GoodsMovementStatus;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoodsMovementRepository
        extends JpaRepository<GoodsMovement, UUID> {

    /*
     * Find movement by generated movement number.
     */
    Optional<GoodsMovement> findByMovementNumber(
            String movementNumber);

    /*
     * Check whether a movement number already exists.
     */
    Boolean existsByMovementNumber(
            String movementNumber);

    /*
     * All movements for a warehouse.
     */
    List<GoodsMovement> findByWarehouseId(
            UUID warehouseId);

    /*
     * Movements by type.
     */
    List<GoodsMovement> findByMovementType(
            GoodsMovementType movementType);

    /*
     * Movements by status.
     */
    List<GoodsMovement> findByStatus(
            GoodsMovementStatus status);


    List<GoodsMovement> findByReferenceNumber(
            String referenceNumber);

    List<GoodsMovement> findByReferenceType(
            String referenceType);

    Optional<GoodsMovement>
    findByReferenceTypeAndReferenceNumber(
            String referenceType,
            String referenceNumber);

    /*
     * Find movements performed by a user.
     */
    List<GoodsMovement> findByPerformedById(
            UUID userId);

    /*
     * Find movements belonging to a warehouse
     * and movement type.
     */
    List<GoodsMovement>
    findByWarehouseIdAndMovementType(
            UUID warehouseId,
            GoodsMovementType movementType);

    /*
     * Find movements belonging to a warehouse
     * and status.
     */
    List<GoodsMovement>
    findByWarehouseIdAndStatus(
            UUID warehouseId,
            GoodsMovementStatus status);
}
