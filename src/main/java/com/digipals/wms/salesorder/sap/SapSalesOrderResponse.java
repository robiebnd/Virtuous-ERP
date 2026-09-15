package com.digipals.wms.salesorder.sap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapSalesOrderResponse(
        @JsonProperty("d") Data data,
        String salesOrderNumber
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            @JsonProperty("SalesOrder") String salesOrderNumber
    ) {
    }

    public String resolvedSalesOrderNumber() {
        if (salesOrderNumber != null && !salesOrderNumber.isBlank()) {
            return salesOrderNumber;
        }
        return data == null ? null : data.salesOrderNumber();
    }
}
