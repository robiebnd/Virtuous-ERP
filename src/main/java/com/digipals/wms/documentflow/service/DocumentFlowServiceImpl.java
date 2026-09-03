package com.digipals.wms.documentflow.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.documentflow.dto.DocumentFlowEntryResponse;
import com.digipals.wms.documentflow.dto.DocumentFlowResponse;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.repository.IncomingPaymentRepository;
import com.digipals.wms.payment.repository.PaymentAllocationRepository;
import com.digipals.wms.dunning.entity.DunningCase;
import com.digipals.wms.dunning.repository.DunningCaseRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentFlowServiceImpl implements DocumentFlowService {

    private final SalesOrderRepository salesOrderRepository;
    private final OutboundDeliveryRepository outboundDeliveryRepository;
    private final BillingDocumentRepository billingDocumentRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;
    private final IncomingPaymentRepository incomingPaymentRepository;
    private final DunningCaseRepository dunningCaseRepository;

    @Override public DocumentFlowResponse getBySalesOrderId(UUID salesOrderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId).orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + salesOrderId));
        return buildFlow(salesOrder);
    }
    @Override public DocumentFlowResponse getByDeliveryId(UUID deliveryId) {
        OutboundDelivery delivery = outboundDeliveryRepository.findById(deliveryId).orElseThrow(() -> new ResourceNotFoundException("Outbound delivery not found: " + deliveryId));
        return buildFlow(delivery.getSalesOrder());
    }
    @Override public DocumentFlowResponse getByBillingDocumentId(UUID billingDocumentId) {
        BillingDocument billing = billingDocumentRepository.findById(billingDocumentId).orElseThrow(() -> new ResourceNotFoundException("Billing document not found: " + billingDocumentId));
        return buildFlow(billing.getOutboundDelivery().getSalesOrder());
    }

    private DocumentFlowResponse buildFlow(SalesOrder salesOrder) {
        List<DocumentFlowEntryResponse> flow = new ArrayList<>();
        flow.add(entry("SALES_ORDER", salesOrder.getId(), salesOrder.getOrderNumber(), salesOrder.getStatus().name(), "ROOT"));

        List<OutboundDelivery> deliveries = outboundDeliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(salesOrder.getId());
        for (OutboundDelivery delivery : deliveries) {
            flow.add(entry("OUTBOUND_DELIVERY", delivery.getId(), delivery.getDeliveryNumber(), delivery.getStatus().name(), "PRECEDES"));
            billingDocumentRepository.findByOutboundDeliveryId(delivery.getId()).ifPresent(billing -> {
                flow.add(entry("BILLING_DOCUMENT", billing.getId(), billing.getBillingNumber(), billing.getStatus().name(), "PRECEDES"));
                List<PaymentAllocation> allocations = paymentAllocationRepository.findActiveByBillingDocumentId(billing.getId(), PaymentStatus.CANCELLED);
                allocations.stream().map(PaymentAllocation::getPayment).distinct().forEach(payment ->
                        flow.add(entry("INCOMING_PAYMENT", payment.getId(), payment.getPaymentNumber(), payment.getStatus().name(), "SETTLES")));
                dunningCaseRepository.findByBillingDocumentIdAndStatusNot(billing.getId(), com.digipals.wms.dunning.entity.DunningStatus.CANCELLED)
                        .ifPresent(dunning -> flow.add(entry("DUNNING_CASE", dunning.getId(), dunning.getDunningNumber(), dunning.getStatus().name(), "FOLLOWS")));
            });
        }
        return DocumentFlowResponse.builder().rootDocumentType("SALES_ORDER").rootDocumentId(salesOrder.getId()).rootDocumentNumber(salesOrder.getOrderNumber()).customerCode(salesOrder.getCustomerCode()).flow(flow).build();
    }

    private DocumentFlowEntryResponse entry(String type, UUID id, String number, String status, String relationship) {
        return DocumentFlowEntryResponse.builder().documentType(type).documentId(id).documentNumber(number).status(status).relationship(relationship).build();
    }
}
