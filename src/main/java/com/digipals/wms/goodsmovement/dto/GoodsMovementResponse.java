package com.digipals.wms.goodsmovement.dto;

import com.digipals.wms.goodsmovement.entity.GoodsMovementStatus;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsMovementResponse {

    private UUID id;

    /*
     * Movement
     */
    private String movementNumber;

    private GoodsMovementType movementType;

    private GoodsMovementStatus status;

    /*
     * Warehouse
     */
    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    /*
     * Reference document
     */
    private String referenceNumber;

    private String referenceType;

    /*
     * User
     */
    private UUID performedById;

    private String performedBy;

    /*
     * Date
     */
    private LocalDateTime movementDate;

    /*
     * Remarks
     */
    private String remarks;

    /*
     * Movement lines
     */
    @Builder.Default
    private List<GoodsMovementLineResponse> lines =
            new ArrayList<>();

    /*
     * Audit
     */
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
