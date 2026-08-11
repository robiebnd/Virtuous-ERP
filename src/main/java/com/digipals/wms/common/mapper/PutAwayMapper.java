package com.digipals.wms.common.mapper;

import com.digipals.wms.putaway.dto.PutAwayLineResponse;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayLine;

import java.util.Collections;
import java.util.List;

public class PutAwayMapper {

    private PutAwayMapper() {
    }

    public static PutAwayResponse toResponse(PutAway putAway) {

        if (putAway == null) {
            return null;
        }

        return PutAwayResponse.builder()
                .id(putAway.getId())
                .putAwayNumber(putAway.getPutAwayNumber())

                .goodsReceiptId(
                        putAway.getGoodsReceipt() != null
                                ? putAway.getGoodsReceipt().getId()
                                : null)

                .grnNumber(
                        putAway.getGoodsReceipt() != null
                                ? putAway.getGoodsReceipt().getGrnNumber()
                                : "")

                .warehouseId(
                        putAway.getWarehouse() != null
                                ? putAway.getWarehouse().getId()
                                : null)

                .warehouseCode(
                        putAway.getWarehouse() != null
                                ? putAway.getWarehouse().getCode()
                                : "")

                .warehouseName(
                        putAway.getWarehouse() != null
                                ? putAway.getWarehouse().getName()
                                : "")

                .status(putAway.getStatus())

                .assignedToId(
                        putAway.getAssignedTo() != null
                                ? putAway.getAssignedTo().getId()
                                : null)

                .assignedTo(
                        putAway.getAssignedTo() != null
                                ? safeUserName(putAway.getAssignedTo())
                                : "")

                .initiatedById(
                        putAway.getInitiatedBy() != null
                                ? putAway.getInitiatedBy().getId()
                                : null)

                .initiatedBy(
                        putAway.getInitiatedBy() != null
                                ? safeUserName(putAway.getInitiatedBy())
                                : "")

                .completedById(
                        putAway.getCompletedBy() != null
                                ? putAway.getCompletedBy().getId()
                                : null)

                .completedBy(
                        putAway.getCompletedBy() != null
                                ? safeUserName(putAway.getCompletedBy())
                                : "")

                .completedAt(putAway.getCompletedAt())

                .remarks(
                        putAway.getRemarks() != null
                                ? putAway.getRemarks()
                                : "")

                .active(putAway.getActive())
                .createdAt(putAway.getCreatedAt())
                .updatedAt(putAway.getUpdatedAt())
                .lines(Collections.emptyList())
                .build();
    }

    public static PutAwayResponse toResponse(
            PutAway putAway,
            List<PutAwayLine> lines) {

        PutAwayResponse response = toResponse(putAway);

        response.setLines(
                lines == null
                        ? Collections.emptyList()
                        : lines.stream()
                                .map(PutAwayMapper::toLineResponse)
                                .toList());

        return response;
    }

    public static PutAwayLineResponse toLineResponse(
            PutAwayLine line) {

        if (line == null) {
            return null;
        }

        return PutAwayLineResponse.builder()
                .id(line.getId())

                .goodsReceiptLineId(
                        line.getGoodsReceiptLine() != null
                                ? line.getGoodsReceiptLine().getId()
                                : null)

                .productId(
                        line.getProduct() != null
                                ? line.getProduct().getId()
                                : null)

                .sku(
                        line.getProduct() != null
                                ? line.getProduct().getSku()
                                : "")

                .productName(
                        line.getProduct() != null
                                ? line.getProduct().getName()
                                : "")

                .fromBinId(
                        line.getFromBin() != null
                                ? line.getFromBin().getId()
                                : null)

                .fromBinCode(
                        line.getFromBin() != null
                                ? line.getFromBin().getCode()
                                : "")

                .toBinId(
                        line.getToBin() != null
                                ? line.getToBin().getId()
                                : null)

                .toBinCode(
                        line.getToBin() != null
                                ? line.getToBin().getCode()
                                : "")

                .plannedQuantity(line.getPlannedQuantity())
                .completedQuantity(
                        line.getCompletedQuantity() != null
                                ? line.getCompletedQuantity()
                                : java.math.BigDecimal.ZERO)
                .build();
    }

    private static String safeUserName(
            com.digipals.wms.users.entity.User user) {

        if (user.getFullName() != null
                && !user.getFullName().isBlank()) {
            return user.getFullName();
        }

        if (user.getUsername() != null
                && !user.getUsername().isBlank()) {
            return user.getUsername();
        }

        return "";
    }
}
