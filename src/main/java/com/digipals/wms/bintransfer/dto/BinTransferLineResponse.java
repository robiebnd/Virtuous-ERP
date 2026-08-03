package com.digipals.wms.bintransfer.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinTransferLineResponse {

    private UUID id;

    private UUID binTransferId;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal quantity;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}