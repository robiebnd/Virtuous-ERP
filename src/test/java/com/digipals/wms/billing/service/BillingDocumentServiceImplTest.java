package com.digipals.wms.billing.service;

import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryItem;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingDocumentServiceImplTest {

    @Mock
    private BillingDocumentRepository billingRepository;

    @Mock
    private OutboundDeliveryRepository deliveryRepository;

    private BillingDocumentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BillingDocumentServiceImpl(billingRepository, deliveryRepository);
    }

    @Test
    void createRejectsDeliveryBeforePgi() {
        UUID deliveryId = UUID.randomUUID();
        OutboundDelivery delivery = OutboundDelivery.builder()
                .id(deliveryId)
                .deliveryNumber("DEL-1001")
                .customerCode("CUST-01")
                .shippingPoint("SP01")
                .status(OutboundDeliveryStatus.PICKED)
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThrows(InvalidWorkflowException.class,
                () -> service.create(new CreateBillingRequest(deliveryId, "USD", null)));
        verify(billingRepository, never()).save(any());
    }

    @Test
    void createCalculatesInvoiceFromDeliveredQuantityAndSalesOrderPrice() {
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        SalesOrderItem orderItem = SalesOrderItem.builder()
                .itemNumber(10)
                .materialCode("MAT-01")
                .unitPrice(new BigDecimal("25.00"))
                .build();

        SalesOrder order = SalesOrder.builder()
                .id(orderId)
                .orderNumber("SO-1001")
                .customerCode("CUST-01")
                .salesOrganization("1000")
                .distributionChannel("10")
                .division("00")
                .items(List.of(orderItem))
                .build();

        OutboundDeliveryItem deliveryItem = OutboundDeliveryItem.builder()
                .itemNumber(10)
                .materialCode("MAT-01")
                .orderedQuantity(new BigDecimal("5"))
                .deliveredQuantity(new BigDecimal("4"))
                .build();

        OutboundDelivery delivery = OutboundDelivery.builder()
                .id(deliveryId)
                .deliveryNumber("DEL-1001")
                .salesOrder(order)
                .customerCode("CUST-01")
                .shippingPoint("SP01")
                .status(OutboundDeliveryStatus.POSTED_GOODS_ISSUE)
                .items(List.of(deliveryItem))
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(billingRepository.findByOutboundDeliveryId(deliveryId)).thenReturn(Optional.empty());
        when(billingRepository.save(any(BillingDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingDocument result = service.create(new CreateBillingRequest(deliveryId, "usd", null));

        assertEquals("USD", result.getCurrency());
        assertEquals("F2", result.getBillingType());
        assertEquals(BillingStatus.DRAFT, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("4"), result.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("25.00"), result.getItems().get(0).getUnitPrice());
        assertEquals(new BigDecimal("100.00"), result.getItems().get(0).getNetValue());
    }

    @Test
    void postMovesDraftBillingToPosted() {
        UUID billingId = UUID.randomUUID();
        BillingDocument billing = BillingDocument.builder()
                .id(billingId)
                .billingNumber("INV-1001")
                .currency("USD")
                .status(BillingStatus.DRAFT)
                .totalAmount(new BigDecimal("100.00"))
                .build();

        billing.setItems(List.of(com.digipals.wms.billing.entity.BillingDocumentItem.builder()
                .itemNumber(10)
                .materialCode("MAT-01")
                .quantity(new BigDecimal("4"))
                .unitPrice(new BigDecimal("25.00"))
                .netValue(new BigDecimal("100.00"))
                .build()));

        when(billingRepository.findById(billingId)).thenReturn(Optional.of(billing));
        when(billingRepository.save(any(BillingDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingDocument result = service.post(billingId);

        assertEquals(BillingStatus.POSTED, result.getStatus());
    }

    @Test
    void postRejectsAlreadyPostedBilling() {
        UUID billingId = UUID.randomUUID();
        BillingDocument billing = BillingDocument.builder()
                .id(billingId)
                .billingNumber("INV-1001")
                .currency("USD")
                .status(BillingStatus.POSTED)
                .totalAmount(new BigDecimal("100.00"))
                .build();

        when(billingRepository.findById(billingId)).thenReturn(Optional.of(billing));

        assertThrows(InvalidWorkflowException.class, () -> service.post(billingId));
        verify(billingRepository, never()).save(any());
    }
}
