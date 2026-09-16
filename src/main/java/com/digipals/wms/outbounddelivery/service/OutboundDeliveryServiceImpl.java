package com.digipals.wms.outbounddelivery.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.entity.BinStatus;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryItem;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.security.CurrentUserService;
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
    private final BinRepository binRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public OutboundDelivery create(CreateOutboundDeliveryRequest request) {
        if (request.salesOrderId() == null) throw new InvalidWorkflowException("Sales order is required.");
        if (request.shippingPoint() == null || request.shippingPoint().isBlank()) throw new InvalidWorkflowException("Shipping point is required.");
        SalesOrder order = salesOrderRepository.findById(request.salesOrderId()).orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + request.salesOrderId()));
        if (order.getStatus() != SalesOrderStatus.CREATED) throw new InvalidWorkflowException("Sales order is not eligible for outbound delivery: " + order.getStatus());
        if (order.getItems() == null || order.getItems().isEmpty()) throw new InvalidWorkflowException("Sales order must contain at least one item.");
        if (deliveryRepository.existsBySalesOrderId(order.getId())) throw new InvalidWorkflowException("An outbound delivery already exists for sales order: " + order.getOrderNumber());

        OutboundDelivery delivery = OutboundDelivery.builder().deliveryNumber(generateDeliveryNumber()).salesOrder(order).customerCode(order.getCustomerCode()).shippingPoint(request.shippingPoint().trim()).requestedDeliveryDate(request.requestedDeliveryDate()).status(OutboundDeliveryStatus.OPEN).build();
        for (SalesOrderItem source : order.getItems()) {
            if (source.getQuantity() == null || source.getQuantity().signum() <= 0) throw new InvalidWorkflowException("Sales order item quantity must be greater than zero: " + source.getItemNumber());
            delivery.addItem(OutboundDeliveryItem.builder().itemNumber(source.getItemNumber()).materialCode(source.getMaterialCode()).orderedQuantity(source.getQuantity()).pickedQuantity(BigDecimal.ZERO).packedQuantity(BigDecimal.ZERO).deliveredQuantity(BigDecimal.ZERO).build());
        }
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery startPicking(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.OPEN);
        validateInventoryAvailability(delivery);
        delivery.setStatus(OutboundDeliveryStatus.PICKING);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public OutboundDelivery confirmPicking(UUID id) {
        OutboundDelivery delivery = get(id);
        requireStatus(delivery, OutboundDeliveryStatus.PICKING);
        if (delivery.getItems() == null || delivery.getItems().isEmpty()) throw new InvalidWorkflowException("Delivery must contain at least one item before picking can be confirmed.");
        validateInventoryAvailability(delivery);
        delivery.getItems().forEach(item -> {
            if (item.getOrderedQuantity() == null || item.getOrderedQuantity().signum() <= 0) throw new InvalidWorkflowException("Delivery item quantity must be greater than zero: " + item.getItemNumber());
            item.setPickedQuantity(item.getOrderedQuantity());
        });
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
            BigDecimal picked = nvl(item.getPickedQuantity());
            BigDecimal ordered = nvl(item.getOrderedQuantity());
            if (picked.signum() < 0 || picked.compareTo(ordered) > 0) throw new InvalidWorkflowException("Invalid picked quantity for delivery item: " + item.getItemNumber());
            if (picked.compareTo(ordered) < 0) throw new InvalidWorkflowException("Cannot pack an incompletely picked delivery item: " + item.getItemNumber());
            item.setPackedQuantity(picked);
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
        if (delivery.getItems() == null || delivery.getItems().isEmpty()) throw new InvalidWorkflowException("Delivery must contain at least one item.");

        User currentUser = currentUserService.getCurrentUser();
        validateInventoryAvailability(delivery);

        for (OutboundDeliveryItem item : delivery.getItems()) {
            BigDecimal packed = nvl(item.getPackedQuantity());
            BigDecimal ordered = nvl(item.getOrderedQuantity());
            if (packed.signum() <= 0 || packed.compareTo(ordered) > 0) throw new InvalidWorkflowException("Invalid packed quantity for delivery item: " + item.getItemNumber());
            if (packed.compareTo(ordered) < 0) throw new InvalidWorkflowException("Cannot post goods issue for an incompletely packed item: " + item.getItemNumber());

            Product product = productRepository.findBySkuIgnoreCase(item.getMaterialCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for delivery item: " + item.getMaterialCode()));
            Bin sourceBin = determineSourceBin(delivery, product, packed);

            inventoryService.issueStock(
                    delivery.getSalesOrder().getWarehouse(),
                    sourceBin,
                    product,
                    packed,
                    delivery.getDeliveryNumber(),
                    "OUTBOUND_DELIVERY",
                    "Goods issue for customer delivery " + delivery.getDeliveryNumber(),
                    currentUser);
            item.setDeliveredQuantity(packed);
        }

        delivery.setGoodsIssueAt(LocalDateTime.now());
        delivery.setStatus(OutboundDeliveryStatus.POSTED_GOODS_ISSUE);
        return deliveryRepository.save(delivery);
    }

    private void validateInventoryAvailability(OutboundDelivery delivery) {
        if (delivery.getSalesOrder().getWarehouse() == null) throw new InvalidWorkflowException("Sales order warehouse is required for goods issue.");
        for (OutboundDeliveryItem item : delivery.getItems()) {
            Product product = productRepository.findBySkuIgnoreCase(item.getMaterialCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for delivery item: " + item.getMaterialCode()));
            BigDecimal quantity = nvl(item.getPackedQuantity());
            if (quantity.signum() <= 0) quantity = nvl(item.getOrderedQuantity());
            determineSourceBin(delivery, product, quantity);
        }
    }

    private Bin determineSourceBin(OutboundDelivery delivery, Product product, BigDecimal quantity) {
        List<com.digipals.wms.inventorybin.entity.InventoryBin> stocks =
                inventoryService.findByWarehouse(delivery.getSalesOrder().getWarehouse().getId()).stream()
                        .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(product.getId()))
                        .filter(i -> i.getBin() != null && Boolean.TRUE.equals(i.getBin().getActive()))
                        .filter(i -> i.getBin().getStatus() == BinStatus.AVAILABLE)
                        .filter(i -> !Boolean.TRUE.equals(i.getBin().getReceivingBin()))
                        .filter(i -> nvl(i.getQuantityOnHand()).subtract(nvl(i.getQuantityReserved())).compareTo(quantity) >= 0)
                        .sorted((a, b) -> nvl(b.getQuantityOnHand()).compareTo(nvl(a.getQuantityOnHand())))
                        .toList();
        if (stocks.isEmpty()) throw new InvalidWorkflowException("Insufficient available stock for " + product.getSku() + ": required " + quantity + ". No eligible source bin has enough stock.");
        return stocks.get(0).getBin();
    }

    @Override @Transactional public OutboundDelivery findById(UUID id) { return get(id); }
    @Override @Transactional public List<OutboundDelivery> findAll() { return deliveryRepository.findAll(); }
    @Override @Transactional public List<OutboundDelivery> findBySalesOrder(UUID salesOrderId) { return deliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(salesOrderId); }

    private OutboundDelivery get(UUID id) { return deliveryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Outbound delivery not found: " + id)); }
    private void requireStatus(OutboundDelivery delivery, OutboundDeliveryStatus expected) { if (delivery.getStatus() != expected) throw new InvalidWorkflowException("Delivery " + delivery.getDeliveryNumber() + " must be in " + expected + " status. Current status: " + delivery.getStatus()); }
    private BigDecimal nvl(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private String generateDeliveryNumber() { return "OD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
}
