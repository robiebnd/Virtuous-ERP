package com.digipals.wms.goodsmovement.repository;

import com.digipals.wms.goodsmovement.entity.GoodsMovementLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoodsMovementLineRepository
        extends JpaRepository<GoodsMovementLine, UUID> {

    /*
     * All lines belonging to a movement.
     */
    List<GoodsMovementLine> findByGoodsMovementId(
            UUID goodsMovementId);

    /*
     * All movement lines for a product.
     */
    List<GoodsMovementLine> findByProductId(
            UUID productId);

    /*
     * All movements originating from a bin.
     */
    List<GoodsMovementLine> findByFromBinId(
            UUID fromBinId);

    /*
     * All movements going into a bin.
     */
    List<GoodsMovementLine> findByToBinId(
            UUID toBinId);

    /*
     * Product movement within a particular
     * Goods Movement.
     */
    List<GoodsMovementLine>
    findByGoodsMovementIdAndProductId(
            UUID goodsMovementId,
            UUID productId);

    /*
     * Count lines belonging to a movement.
     */
    Long countByGoodsMovementId(
            UUID goodsMovementId);

    /*
     * Check whether a movement already contains
     * a particular product.
     */
    Boolean existsByGoodsMovementIdAndProductId(
            UUID goodsMovementId,
            UUID productId);

    /*
     * Find movements involving a product
     * from one bin.
     */
    List<GoodsMovementLine>
    findByProductIdAndFromBinId(
            UUID productId,
            UUID fromBinId);

    /*
     * Find movements involving a product
     * into one bin.
     */
    List<GoodsMovementLine>
    findByProductIdAndToBinId(
            UUID productId,
            UUID toBinId);
}

