package com.digipals.wms.purchaserequisition.dto;

import lombok.Data;

import java.util.UUID;


@Data
public class CreatePurchaseRequisitionRequest {

    private UUID warehouseId;

    private String department;

    private String remarks;
}
