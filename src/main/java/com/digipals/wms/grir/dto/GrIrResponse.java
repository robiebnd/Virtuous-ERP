package com.digipals.wms.grir.dto;

import com.digipals.wms.grir.entity.GrIrStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class GrIrResponse {
    UUID purchaseOrderId;
    String purchaseOrderNumber;
    BigDecimal poValue;
    BigDecimal receivedValue;
    BigDecimal invoicedValue;
    BigDecimal grIrBalance;
    GrIrStatus status;
    String remarks;
}
