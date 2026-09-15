package com.digipals.wms.procurementclosure.dto;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder
public class ProcurementClosureResponse {
    UUID purchaseOrderId;
    String purchaseOrderNumber;
    String purchaseOrderStatus;
    String grIrStatus;
    boolean invoicesPaid;
    boolean vendorEvaluated;
    boolean closed;
    String message;
}
