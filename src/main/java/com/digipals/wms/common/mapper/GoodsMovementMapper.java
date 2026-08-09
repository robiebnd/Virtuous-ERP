package com.digipals.wms.common.mapper;

import com.digipals.wms.goodsmovement.dto.GoodsMovementLineResponse;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovement;
import com.digipals.wms.goodsmovement.entity.GoodsMovementLine;

import java.util.Collections;
import java.util.List;

public final class GoodsMovementMapper {

    private GoodsMovementMapper() {
    }

    /**
     * Converts a GoodsMovement entity into a response DTO.
     *
     * Lines are not loaded by this method.
     * Use the overloaded toResponse() method when lines
     * are required.
     */
    public static GoodsMovementResponse toResponse(
            GoodsMovement movement) {

        if (movement == null) {
            return null;
        }

        return GoodsMovementResponse.builder()

                /*
                 * Identity
                 */
                .id(
                        movement.getId())

                .movementNumber(
                        movement.getMovementNumber())

                /*
                 * Movement
                 */
                .movementType(
                        movement.getMovementType())

                .status(
                        movement.getStatus())

                /*
                 * Warehouse
                 */
                .warehouseId(
                        movement.getWarehouse() != null
                                ? movement.getWarehouse().getId()
                                : null)

                .warehouseCode(
                        movement.getWarehouse() != null
                                ? movement.getWarehouse().getCode()
                                : null)

                .warehouseName(
                        movement.getWarehouse() != null
                                ? movement.getWarehouse().getName()
                                : null)

                /*
                 * Reference
                 */
                .referenceNumber(
                        movement.getReferenceNumber())

                .referenceType(
                        movement.getReferenceType())

                /*
                 * Performed By
                 */
                .performedById(
                        movement.getPerformedBy() != null
                                ? movement.getPerformedBy().getId()
                                : null)

                .performedBy(
                        movement.getPerformedBy() != null
                                ? movement.getPerformedBy().getUsername()
                                : null)

                /*
                 * Date
                 */
                .movementDate(
                        movement.getMovementDate())

                /*
                 * Remarks
                 */
                .remarks(
                        movement.getRemarks())

                /*
                 * Lines
                 */
                .lines(
                        Collections.emptyList())

                /*
                 * Audit
                 */
                .active(
                        movement.getActive())

                .createdAt(
                        movement.getCreatedAt())

                .updatedAt(
                        movement.getUpdatedAt())

                .build();
    }

    /**
     * Converts a GoodsMovement entity and its lines
     * into a complete response DTO.
     */
    public static GoodsMovementResponse toResponse(
            GoodsMovement movement,
            List<GoodsMovementLine> lines) {

        GoodsMovementResponse response =
                toResponse(movement);

        if (response == null) {
            return null;
        }

        response.setLines(
                lines == null
                        ? Collections.emptyList()
                        : lines.stream()
                                .map(
                                        GoodsMovementMapper::toLineResponse)
                                .toList());

        return response;
    }

    /**
     * Converts a GoodsMovementLine entity
     * into a response DTO.
     */
    public static GoodsMovementLineResponse toLineResponse(
            GoodsMovementLine line) {

        if (line == null) {
            return null;
        }

        return GoodsMovementLineResponse.builder()

                /*
                 * Identity
                 */
                .id(
                        line.getId())

                /*
                 * Product
                 */
                .productId(
                        line.getProduct() != null
                                ? line.getProduct().getId()
                                : null)

                .sku(
                        line.getProduct() != null
                                ? line.getProduct().getSku()
                                : null)

                .productName(
                        line.getProduct() != null
                                ? line.getProduct().getName()
                                : null)

                /*
                 * From Bin
                 */
                .fromBinId(
                        line.getFromBin() != null
                                ? line.getFromBin().getId()
                                : null)

                .fromBinCode(
                        line.getFromBin() != null
                                ? line.getFromBin().getCode()
                                : null)

                .fromBinName(
                        line.getFromBin() != null
                                ? line.getFromBin().getName()
                                : null)

                /*
                 * To Bin
                 */
                .toBinId(
                        line.getToBin() != null
                                ? line.getToBin().getId()
                                : null)

                .toBinCode(
                        line.getToBin() != null
                                ? line.getToBin().getCode()
                                : null)

                .toBinName(
                        line.getToBin() != null
                                ? line.getToBin().getName()
                                : null)

                /*
                 * Quantity
                 */
                .quantity(
                        line.getQuantity())

                /*
                 * Cost
                 */
                .unitCost(
                        line.getUnitCost())

                /*
                 * Remarks
                 */
                .remarks(
                        line.getRemarks())

                .build();
    }

    /**
     * Converts a list of GoodsMovementLine entities.
     */
    public static List<GoodsMovementLineResponse> toLineResponses(
            List<GoodsMovementLine> lines) {

        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }

        return lines.stream()
                .map(
                        GoodsMovementMapper::toLineResponse)
                .toList();
    }
}

