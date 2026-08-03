package com.digipals.wms.bintransfer.dto;

import com.digipals.wms.bintransfer.entity.BinTransferStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinTransferResponse {

    private UUID id;

    private String transferNumber;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private UUID fromBinId;

    private String fromBinCode;

    private String fromBinName;

    private UUID toBinId;

    private String toBinCode;

    private String toBinName;

    private BinTransferStatus status;

    private String remarks;

    private LocalDateTime transferDate;

    private LocalDateTime approvedAt;

    private LocalDateTime postedAt;

    private UUID approvedById;

    private String approvedBy;

    private UUID postedById;

    private String postedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}