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
                                        : null)
                                .warehouseId(
                                                putAway.getWarehouse() != null
                                                                ? putAway.getWarehouse().getId()
                                                                : null)

                                .warehouseName(
                                                putAway.getWarehouse() != null
                                                                ? putAway.getWarehouse().getName()
                                                                : null)

                                .warehouseCode(
                                                putAway.getWarehouse() != null
                                                                ? putAway.getWarehouse().getCode()
                                                                : null)

                                .status(
                                                putAway.getStatus())

                                .assignedToId(
                                                putAway.getAssignedTo() != null
                                                                ? putAway.getAssignedTo().getId()
                                                                : null)

                                .assignedTo(
                                                putAway.getAssignedTo() != null
                                                                ? putAway.getAssignedTo().getFullName()
                                                                : null)

                                .initiatedById(
                                                putAway.getInitiatedBy() != null
                                                                ? putAway.getInitiatedBy().getId()
                                                                : null)

                                .initiatedBy(
                                                putAway.getInitiatedBy() != null
                                                                ? putAway.getInitiatedBy().getFullName()
                                                                : null)

                                .completedById(
                                                putAway.getCompletedBy() != null
                                                                ? putAway.getCompletedBy().getId()
                                                                : null)

                                .completedBy(
                                                putAway.getCompletedBy() != null
                                                                ? putAway.getCompletedBy().getFullName()
                                                                : null)

                                .warehouseCode(
                                                putAway.getWarehouse() != null
                                                                ? putAway.getWarehouse().getCode()
                                                                : null)

                                .active(
                                                putAway.getActive())

                                .createdAt(
                                                putAway.getCreatedAt())

                                .updatedAt(
                                                putAway.getUpdatedAt())

                                .lines(Collections.emptyList())

                                .build();
        }

        public static PutAwayResponse toResponse(
                        PutAway putAway,
                        List<PutAwayLine> lines) {

                PutAwayResponse response = toResponse(putAway);

                response.setLines(
                                lines.stream()
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
                                                line.getGoodsReceiptLine().getId())

                                .productId(
                                                line.getProduct().getId())

                                .sku(
                                                line.getProduct() != null
                                                                ? line.getProduct().getSku()
                                                                : null)

                                .productName(
                                                line.getProduct() != null
                                                                ? line.getProduct().getName()
                                                                : null)

                                .fromBinId(
                                                line.getFromBin().getId())

                                .fromBinCode(
                                                line.getFromBin().getCode())

                                .toBinId(
                                                line.getToBin() != null
                                                                ? line.getToBin().getId()
                                                                : null)

                                .toBinCode(
                                                line.getToBin() != null
                                                                ? line.getToBin().getCode()
                                                                : null)

                                .plannedQuantity(line.getPlannedQuantity())

                                .completedQuantity(line.getCompletedQuantity())

                                .build();
        }
}
