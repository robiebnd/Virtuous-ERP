package com.digipals.wms.documentflow.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.documentflow.dto.DocumentFlowEntryResponse;
import com.digipals.wms.documentflow.dto.DocumentFlowResponse;
import com.digipals.wms.dunning.entity.DunningStatus;
import com.digipals.wms.dunning.repository.DunningCaseRepository;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.repository.PaymentAllocationRepository;
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
    private final DunningCaseRepository dunningCaseRepository;

    @Override public DocumentFlowResponse getBySalesOrderId(UUID id) { return buildFlow(salesOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + id))); }
    @Override public DocumentFlowResponse getByDeliveryId(UUID id) { OutboundDelivery d = outboundDeliveryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Outbound delivery not found: " + id)); return buildFlow(d.getSalesOrder()); }
    @Override public DocumentFlowResponse getByBillingDocumentId(UUID id) { BillingDocument b = billingDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Billing document not found: " + id)); return buildFlow(b.getOutboundDelivery().getSalesOrder()); }

    private DocumentFlowResponse buildFlow(SalesOrder so) {
        List<DocumentFlowEntryResponse> flow = new ArrayList<>();
        flow.add(entry("SALES_ORDER", so.getId(), so.getOrderNumber(), so.getStatus().name(), "ROOT"));
        for (OutboundDelivery d : outboundDeliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(so.getId())) {
            flow.add(entry("OUTBOUND_DELIVERY", d.getId(), d.getDeliveryNumber(), d.getStatus().name(), "PRECEDES"));
            billingDocumentRepository.findByOutboundDeliveryId(d.getId()).ifPresent(b -> {
                flow.add(entry("BILLING_DOCUMENT", b.getId(), b.getBillingNumber(), b.getStatus().name(), "PRECEDES"));
                paymentAllocationRepository.findActiveByBillingDocumentId(b.getId(), PaymentStatus.CANCELLED).stream()
                        .map(PaymentAllocation::getPayment).distinct()
                        .forEach(p -> flow.add(entry("INCOMING_PAYMENT", p.getId(), p.getPaymentNumber(), p.getStatus().name(), "SETTLES")));
                dunningCaseRepository.findByBillingDocumentIdAndStatusNot(b.getId(), DunningStatus.CANCELLED)
                        .ifPresent(x -> flow.add(entry("DUNNING_CASE", x.getId(), x.getDunningNumber(), x.getStatus().name(), "FOLLOWS")));
            });
        }
        return DocumentFlowResponse.builder().rootDocumentType("SALES_ORDER").rootDocumentId(so.getId()).rootDocumentNumber(so.getOrderNumber()).customerCode(so.getCustomerCode()).flow(flow).build();
    }
    private DocumentFlowEntryResponse entry(String type, UUID id, String number, String status, String relationship) { return DocumentFlowEntryResponse.builder().documentType(type).documentId(id).documentNumber(number).status(status).relationship(relationship).build(); }
}
