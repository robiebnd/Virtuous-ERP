package com.digipals.wms.outbounddelivery.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryItem;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboundDeliveryServiceImpl implements OutboundDeliveryService {

    private final OutboundDeliveryRepository deliveryRepository;
    private final SalesOrderRepository salesOrderRepository;

    @Override
    @Transactional
    public OutboundDelivery create(CreateOutboundDeliveryRequest request) {
        if (request.salesOrderId() == null) {
            throw new InvalidWorkflowException("Sales order is required.");
        }

        SalesOrder order = salesOrderRepository.findById(request.salesOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + request.salesOrderId()));

        if (order.getStatus() != SalesOrderStatus.CREATED && order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new InvalidWorkflowException("Sales order is not eligible for outbound delivery: " + order.getStatus());
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new InvalidWorkflowException("Sales order must contain at least one item.");
        }
        if (deliveryRepository.existsBySalesOrderId(order.getId())) {
            throw new InvalidWorkflowException("An outbound delivery already exists for sales order: " + order.getOrderNumber());
        }

        OutboundDelivery delivery = OutboundDelivery.builder()
                .deliveryNumber(generateDeliveryNumber())
                .salesOrder(order)
                .customerCode(order.getCustomerCode())
                .shippingPoint(request.shippingPoint().trim())
                .requestedDeliveryDate(request.requestedDeliveryDate())
                .status(OutboundDeliveryStatus.OPEN)
                .build();

        for (SalesOrderItem source : order.getItems()) {
            if (source.getQuantity() == null || source.getQuantity().signum() <= 0) {
                throw new InvalidWorkflowException("Sales order item quantity must be greater than zero: " + source.getItemNumber());
            }
            delivery.addItem(OutboundDeliveryItem.builder()
                    .itemNumber(source.getItemNumber())
                    .materialCode(source.getMaterialCode())
                    .orderedQuantity(source.getQuantity())
                    .pickedQuantity(BigDecimal.ZERO)
                    .packedQuantity(BigDecimal.ZERO)
                    .deliveredQuantity(BigDecimal.ZERO)
                    .build());
        }

        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery startPicking(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.OPEN);
        delivery.setStatus(OutboundDeliveryStatus.PICKING);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery confirmPicking(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.PICKING);
        delivery.getItems().forEach(item -> item.setPickedQuantity(item.getOrderedQuantity()));
        delivery.setPickedAt(LocalDateTime.now());
        delivery.setStatus(OutboundDeliveryStatus.PICKED);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery confirmPacking(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.PICKED);
        delivery.getItems().forEach(item -> {
            if (item.getPickedQuantity().compareTo(item.getOrderedQuantity()) < 0) {
                throw new InvalidWorkflowException("Cannot pack an incompletely picked delivery item: " + item.getItemNumber());
            }
            item.setPackedQuantity(item.getPickedQuantity());
        });
        delivery.setPackedAt(LocalDateTime.now());
        delivery.setStatus(OutboundDeliveryStatus.PACKED);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery postGoodsIssue(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.PACKED);
        delivery.getItems().forEach(item -> {
            if (item.getPackedQuantity().compareTo(item.getOrderedQuantity()) < 0) {
                throw new InvalidWorkflowException("Cannot post goods issue for an incompletely packed item: " + item.getItemNumber());
            }
            item.setDeliveredQuantity(item.getPackedQuantity());
        });
        delivery.setGoodsIssueAt(LocalDateTime.now());
        delivery.setStatus(OutboundDeliveryStatus.POSTED_GOODS_ISSUE);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery findById(UUID id) {
        return get(id);
    }

    @Override
    @Transactional
    public List<OutboundDelivery> findAll() {
        return deliveryRepository.findAll();
    }

    @Override
    @Transactional
    public List<OutboundDelivery> findBySalesOrder(UUID salesOrderId) {
        return deliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(salesOrderId);
    }

    private OutboundDelivery get(UUID id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound delivery not found: " + id));
    }

    private void requireStatus(OutboundDelivery delivery, OutboundDeliveryStatus expected) {
        if (delivery.getStatus() != expected) {
            throw new InvalidWorkflowException("Delivery " + delivery.getDeliveryNumber() + " must be in " + expected + " status. Current status: " + delivery.getStatus());
        }
    }

    private String generateDeliveryNumber() {
        return "OD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
