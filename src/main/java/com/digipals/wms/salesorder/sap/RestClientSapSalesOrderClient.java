package com.digipals.wms.salesorder.sap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientSapSalesOrderClient implements SapSalesOrderClient {

    private final RestClient restClient;
    private final String salesOrderPath;
    private final String username;
    private final String password;

    public RestClientSapSalesOrderClient(
            @Value("${sap.base-url:}") String baseUrl,
            @Value("${sap.sales-order.path:/sap/opu/odata/sap/API_SALES_ORDER_SRV/A_SalesOrder}") String salesOrderPath,
            @Value("${sap.username:}") String username,
            @Value("${sap.password:}") String password) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.salesOrderPath = salesOrderPath;
        this.username = username;
        this.password = password;
    }

    @Override
    public SapSalesOrderResponse createSalesOrder(SapSalesOrderRequest request) {
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("SAP credentials are not configured");
        }

        SapSalesOrderResponse response = restClient.post()
                .uri(salesOrderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(username, password))
                .body(request)
                .retrieve()
                .body(SapSalesOrderResponse.class);

        if (response == null || response.resolvedSalesOrderNumber() == null
                || response.resolvedSalesOrderNumber().isBlank()) {
            throw new IllegalStateException("SAP did not return a sales order number");
        }

        return response;
    }
}
