package com.digipals.wms.salesorder.controller;

import com.digipals.wms.salesorder.dto.CreateSalesOrderItemRequest;
import com.digipals.wms.salesorder.dto.CreateSalesOrderRequest;
import com.digipals.wms.salesorder.dto.SalesOrderItemResponse;
import com.digipals.wms.salesorder.dto.SalesOrderResponse;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import com.digipals.wms.salesorder.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public SalesOrderResponse create(@Valid @RequestBody CreateSalesOrderRequest request) {
        return toResponse(salesOrderService.create(request));
    }

    @GetMapping
    public List<SalesOrderResponse> findAll() {
        return salesOrderService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SalesOrderResponse findById(@PathVariable UUID id) {
        return toResponse(salesOrderService.findById(id));
    }

    @GetMapping("/number/{orderNumber}")
    public SalesOrderResponse findByOrderNumber(@PathVariable String orderNumber) {
        return toResponse(salesOrderService.findByOrderNumber(orderNumber));
    }

    @GetMapping("/customer/{customerCode}")
    public List<SalesOrderResponse> findByCustomer(@PathVariable String customerCode) {
        return salesOrderService.findByCustomerCode(customerCode).stream()
                .map(this::toResponse)
                .toList();
    }

    private SalesOrderResponse toResponse(SalesOrder order) {
        List<SalesOrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new SalesOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getSapOrderNumber(),
                order.getCustomerCode(),
                order.getSalesOrganization(),
                order.getDistributionChannel(),
                order.getDivision(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderDate(),
                order.getRemarks(),
                items);
    }

    private SalesOrderItemResponse toItemResponse(SalesOrderItem item) {
        return new SalesOrderItemResponse(
                item.getId(),
                item.getItemNumber(),
                item.getMaterialCode(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getNetValue());
    }
}
