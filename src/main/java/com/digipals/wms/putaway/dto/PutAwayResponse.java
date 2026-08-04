package com.digipals.wms.putaway.dto;

import com.digipals.wms.putaway.entity.PutAwayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutAwayResponse {

    /*
     * Header
     */
    private UUID id;

    private String putAwayNumber;

    /*
     * Goods Receipt
     */
    private UUID goodsReceiptId;

    private String grnNumber;

    /*
     * Warehouse
     */
    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    /*
     * Status
     */
    private PutAwayStatus status;

    /*
     * Assigned Operator
     */
    private UUID assignedToId;

    private String assignedTo;

    /*
     * Initiated By
     */
    private UUID initiatedById;

    private String initiatedBy;

    /*
     * Completed By
     */
    private UUID completedById;

    private String completedBy;

    private LocalDateTime completedAt;

    /*
     * Remarks
     */
    private String remarks;

    /*
     * Put Away Lines
     */
    @Builder.Default
    private List<PutAwayLineResponse> lines =
            new ArrayList<>();

    /*
     * Audit
     */
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}