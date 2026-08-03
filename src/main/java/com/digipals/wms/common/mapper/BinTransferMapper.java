package com.digipals.wms.common.mapper;

import com.digipals.wms.bintransfer.dto.BinTransferResponse;
import com.digipals.wms.bintransfer.entity.BinTransfer;

public class BinTransferMapper {

    public static BinTransferResponse toResponse(BinTransfer entity) {

        if (entity == null) {
            return null;
        }

        return BinTransferResponse.builder()

                .id(entity.getId())

                .transferNumber(
                        entity.getTransferNumber())

                .warehouseId(
                        entity.getWarehouse() != null
                                ? entity.getWarehouse().getId()
                                : null)

                .warehouseCode(
                        entity.getWarehouse() != null
                                ? entity.getWarehouse().getCode()
                                : null)

                .warehouseName(
                        entity.getWarehouse() != null
                                ? entity.getWarehouse().getName()
                                : null)

                .fromBinId(
                        entity.getFromBin() != null
                                ? entity.getFromBin().getId()
                                : null)

                .fromBinCode(
                        entity.getFromBin() != null
                                ? entity.getFromBin().getCode()
                                : null)

                .fromBinName(
                        entity.getFromBin() != null
                                ? entity.getFromBin().getName()
                                : null)

                .toBinId(
                        entity.getToBin() != null
                                ? entity.getToBin().getId()
                                : null)

                .toBinCode(
                        entity.getToBin() != null
                                ? entity.getToBin().getCode()
                                : null)

                .toBinName(
                        entity.getToBin() != null
                                ? entity.getToBin().getName()
                                : null)

                .status(
                        entity.getStatus())

                .remarks(
                        entity.getRemarks())

                .transferDate(
                        entity.getTransferDate())

                .approvedAt(
                        entity.getApprovedAt())

                .postedAt(
                        entity.getPostedAt())

                .approvedById(
                        entity.getApprovedBy() != null
                                ? entity.getApprovedBy().getId()
                                : null)

                .approvedBy(
                        entity.getApprovedBy() != null
                                ? entity.getApprovedBy().getUsername()
                                : null)

                .postedById(
                        entity.getPostedBy() != null
                                ? entity.getPostedBy().getId()
                                : null)

                .postedBy(
                        entity.getPostedBy() != null
                                ? entity.getPostedBy().getUsername()
                                : null)

                .createdAt(
                        entity.getCreatedAt())

                .updatedAt(
                        entity.getUpdatedAt())

                .build();
    }
}
