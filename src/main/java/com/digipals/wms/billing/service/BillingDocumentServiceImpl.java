package com.digipals.wms.billing.service;

import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingDocumentItem;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryItem;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingDocumentServiceImpl implements BillingDocumentService {

    private final BillingDocumentRepository billingDocumentRepository;
    private final OutboundDeliveryRepository outboundDeliveryRepository;

    @Override
    @Transactional
    public BillingDocument create(CreateBillingRequest request) {
        if (request.outboundDeliveryId() == null) throw new InvalidWorkflowException("Outbound delivery is required for billing.");
        if (request.currency() == null || request.currency().isBlank()) throw new InvalidWorkflowException("Currency is required for billing.");

        OutboundDelivery delivery = outboundDeliveryRepository.findById(request.outboundDeliveryId())
                .orElseThrow(() -> new ResourceNotFoundException("Outbound delivery not found: " + request.outboundDeliveryId()));
        if (delivery.getStatus() != OutboundDeliveryStatus.POSTED_GOODS_ISSUE) throw new InvalidWorkflowException("Billing can only be created after Post Goods Issue.");
        if (billingDocumentRepository.findByOutboundDeliveryId(delivery.getId()).isPresent()) throw new InvalidWorkflowException("A billing document already exists for outbound delivery: " + delivery.getDeliveryNumber());

        LocalDateTime billingDate = LocalDateTime.now();
        LocalDateTime dueDate = request.dueDate() == null ? billingDate.plusDays(30) : request.dueDate();
        if (dueDate.isBefore(billingDate)) throw new InvalidWorkflowException("Billing due date cannot be before billing date.");

        BillingDocument billing = BillingDocument.builder()
                .billingNumber(generateBillingNumber()).outboundDelivery(delivery)
                .customerCode(delivery.getCustomerCode()).billingType("F2")
                .currency(request.currency().trim().toUpperCase()).status(BillingStatus.DRAFT)
                .billingDate(billingDate).dueDate(dueDate).remarks(delivery.getRemarks())
                .totalAmount(BigDecimal.ZERO).build();

        BigDecimal total = BigDecimal.ZERO;
        int itemNumber = 10;
        for (OutboundDeliveryItem deliveryItem : delivery.getItems()) {
            BigDecimal quantity = deliveryItem.getDeliveredQuantity();
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Delivered quantity must be greater than zero for delivery item: " + deliveryItem.getItemNumber());
            if (deliveryItem.getOrderedQuantity() != null && quantity.compareTo(deliveryItem.getOrderedQuantity()) > 0) throw new InvalidWorkflowException("Delivered quantity cannot exceed ordered quantity for delivery item: " + deliveryItem.getItemNumber());

            SalesOrderItem sourceItem = findSalesOrderItem(delivery, deliveryItem);
            BigDecimal unitPrice = sourceItem.getUnitPrice() == null ? BigDecimal.ZERO : sourceItem.getUnitPrice();
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new InvalidWorkflowException("Unit price cannot be negative for delivery item: " + deliveryItem.getItemNumber());
            BigDecimal netValue = unitPrice.multiply(quantity);
            billing.addItem(BillingDocumentItem.builder().itemNumber(itemNumber).materialCode(deliveryItem.getMaterialCode()).quantity(quantity).unitPrice(unitPrice).netValue(netValue).build());
            total = total.add(netValue);
            itemNumber += 10;
        }
        if (billing.getItems().isEmpty()) throw new InvalidWorkflowException("Billing document must contain at least one delivered item.");
        billing.setTotalAmount(total);
        return billingDocumentRepository.save(billing);
    }

    @Override
    @Transactional
    public BillingDocument post(UUID id) {
        BillingDocument billing = findById(id);
        if (billing.getStatus() != BillingStatus.DRAFT) throw new InvalidWorkflowException("Only draft billing documents can be posted.");
        if (billing.getItems().isEmpty()) throw new InvalidWorkflowException("Billing document must contain at least one item.");
        if (billing.getTotalAmount() == null || billing.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) throw new InvalidWorkflowException("Billing total cannot be negative.");
        if (billing.getDueDate() == null || billing.getDueDate().isBefore(billing.getBillingDate())) throw new InvalidWorkflowException("Billing due date is invalid.");
        billing.setStatus(BillingStatus.POSTED);
        return billingDocumentRepository.save(billing);
    }

    @Override @Transactional public BillingDocument findById(UUID id) { return billingDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Billing document not found: " + id)); }
    @Override @Transactional public BillingDocument findByBillingNumber(String billingNumber) { return billingDocumentRepository.findByBillingNumber(billingNumber).orElseThrow(() -> new ResourceNotFoundException("Billing document not found: " + billingNumber)); }
    @Override @Transactional public List<BillingDocument> findAll() { return billingDocumentRepository.findAll(); }
    @Override @Transactional public List<BillingDocument> findByCustomerCode(String customerCode) { return billingDocumentRepository.findByCustomerCodeOrderByBillingDateDesc(customerCode); }

    private SalesOrderItem findSalesOrderItem(OutboundDelivery delivery, OutboundDeliveryItem deliveryItem) {
        return delivery.getSalesOrder().getItems().stream().filter(item -> item.getItemNumber().equals(deliveryItem.getItemNumber()) || item.getMaterialCode().equalsIgnoreCase(deliveryItem.getMaterialCode())).findFirst().orElseThrow(() -> new InvalidWorkflowException("Sales order item not found for delivery item: " + deliveryItem.getItemNumber()));
    }
    private String generateBillingNumber() { return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
}
