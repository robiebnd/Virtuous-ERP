package com.digipals.wms.outbounddelivery.controller;

import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.dto.OutboundDeliveryResponse;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryItem;
import com.digipals.wms.outbounddelivery.service.OutboundDeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/outbound-deliveries")
@RequiredArgsConstructor
public class OutboundDeliveryController {

    private final OutboundDeliveryService service;

    @PostMapping
    public OutboundDeliveryResponse create(@Valid @RequestBody CreateOutboundDeliveryRequest request) {
        return toResponse(service.create(request));
    }

    @GetMapping
    public List<OutboundDeliveryResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public OutboundDeliveryResponse findById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    @GetMapping("/sales-order/{salesOrderId}")
    public List<OutboundDeliveryResponse> findBySalesOrder(@PathVariable UUID salesOrderId) {
        return service.findBySalesOrder(salesOrderId).stream().map(this::toResponse).toList();
    }

    @PostMapping("/{id}/start-picking")
    public OutboundDeliveryResponse startPicking(@PathVariable UUID id) {
        return toResponse(service.startPicking(id));
    }

    @PostMapping("/{id}/confirm-picking")
    public OutboundDeliveryResponse confirmPicking(@PathVariable UUID id) {
        return toResponse(service.confirmPicking(id));
    }

    @PostMapping("/{id}/confirm-packing")
    public OutboundDeliveryResponse confirmPacking(@PathVariable UUID id) {
        return toResponse(service.confirmPacking(id));
    }

    @PostMapping("/{id}/post-goods-issue")
    public OutboundDeliveryResponse postGoodsIssue(@PathVariable UUID id) {
        return toResponse(service.postGoodsIssue(id));
    }

    private OutboundDeliveryResponse toResponse(OutboundDelivery delivery) {
        return new OutboundDeliveryResponse(
                delivery.getId(),
                delivery.getDeliveryNumber(),
                delivery.getSalesOrder().getId(),
                delivery.getSalesOrder().getOrderNumber(),
                delivery.getCustomerCode(),
                delivery.getShippingPoint(),
                delivery.getRequestedDeliveryDate(),
                delivery.getStatus(),
                delivery.getPickedAt(),
                delivery.getPackedAt(),
                delivery.getGoodsIssueAt(),
                delivery.getItems().stream().map(this::toItem).toList());
    }

    private OutboundDeliveryResponse.Item toItem(OutboundDeliveryItem item) {
        return new OutboundDeliveryResponse.Item(
                item.getId(),
                item.getItemNumber(),
                item.getMaterialCode(),
                item.getOrderedQuantity(),
                item.getPickedQuantity(),
                item.getPackedQuantity(),
                item.getDeliveredQuantity());
    }
}
