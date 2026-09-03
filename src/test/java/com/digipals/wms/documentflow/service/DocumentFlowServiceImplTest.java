package com.digipals.wms.documentflow.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.documentflow.dto.DocumentFlowResponse;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentFlowServiceImplTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private OutboundDeliveryRepository deliveryRepository;

    @Mock
    private BillingDocumentRepository billingRepository;

    private DocumentFlowServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DocumentFlowServiceImpl(salesOrderRepository, deliveryRepository, billingRepository);
    }

    @Test
    void salesOrderFlowIncludesDeliveryAndBilling() {
        UUID orderId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        UUID billingId = UUID.randomUUID();

        SalesOrder order = SalesOrder.builder()
                .id(orderId)
                .orderNumber("SO-1001")
                .customerCode("CUST-01")
                .salesOrganization("1000")
                .distributionChannel("10")
                .division("00")
                .status(SalesOrderStatus.CREATED)
                .build();

        OutboundDelivery delivery = OutboundDelivery.builder()
                .id(deliveryId)
                .deliveryNumber("DEL-1001")
                .salesOrder(order)
                .customerCode("CUST-01")
                .shippingPoint("SP01")
                .status(OutboundDeliveryStatus.POSTED_GOODS_ISSUE)
                .build();

        BillingDocument billing = BillingDocument.builder()
                .id(billingId)
                .billingNumber("INV-1001")
                .outboundDelivery(delivery)
                .customerCode("CUST-01")
                .currency("USD")
                .status(BillingStatus.POSTED)
                .build();

        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(orderId)).thenReturn(List.of(delivery));
        when(billingRepository.findByOutboundDeliveryId(deliveryId)).thenReturn(Optional.of(billing));

        DocumentFlowResponse result = service.getBySalesOrderId(orderId);

        assertEquals("SALES_ORDER", result.getRootDocumentType());
        assertEquals(orderId, result.getRootDocumentId());
        assertEquals("SO-1001", result.getRootDocumentNumber());
        assertEquals("CUST-01", result.getCustomerCode());
        assertEquals(3, result.getFlow().size());
        assertEquals("SALES_ORDER", result.getFlow().get(0).getDocumentType());
        assertEquals("OUTBOUND_DELIVERY", result.getFlow().get(1).getDocumentType());
        assertEquals("BILLING_DOCUMENT", result.getFlow().get(2).getDocumentType());
    }

    @Test
    void deliveryLookupBuildsFlowFromItsSalesOrder() {
        UUID orderId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        SalesOrder order = SalesOrder.builder()
                .id(orderId)
                .orderNumber("SO-1002")
                .customerCode("CUST-02")
                .salesOrganization("1000")
                .distributionChannel("10")
                .division("00")
                .status(SalesOrderStatus.CREATED)
                .build();

        OutboundDelivery delivery = OutboundDelivery.builder()
                .id(deliveryId)
                .deliveryNumber("DEL-1002")
                .salesOrder(order)
                .customerCode("CUST-02")
                .shippingPoint("SP01")
                .status(OutboundDeliveryStatus.PACKED)
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(orderId)).thenReturn(List.of(delivery));
        when(billingRepository.findByOutboundDeliveryId(deliveryId)).thenReturn(Optional.empty());

        DocumentFlowResponse result = service.getByDeliveryId(deliveryId);

        assertEquals(orderId, result.getRootDocumentId());
        assertEquals(2, result.getFlow().size());
        assertEquals("DEL-1002", result.getFlow().get(1).getDocumentNumber());
    }
}
