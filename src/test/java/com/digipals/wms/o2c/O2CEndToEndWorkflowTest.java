package com.digipals.wms.o2c;

import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.billing.service.BillingDocumentServiceImpl;
import com.digipals.wms.documentflow.dto.DocumentFlowResponse;
import com.digipals.wms.documentflow.service.DocumentFlowServiceImpl;
import com.digipals.wms.dunning.dto.CreateDunningRequest;
import com.digipals.wms.dunning.entity.DunningCase;
import com.digipals.wms.dunning.entity.DunningStatus;
import com.digipals.wms.dunning.repository.DunningCaseRepository;
import com.digipals.wms.dunning.service.DunningServiceImpl;
import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.outbounddelivery.service.OutboundDeliveryServiceImpl;
import com.digipals.wms.payment.dto.CashApplicationRequest;
import com.digipals.wms.payment.dto.CreateIncomingPaymentRequest;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.repository.IncomingPaymentRepository;
import com.digipals.wms.payment.repository.PaymentAllocationRepository;
import com.digipals.wms.payment.service.CashApplicationServiceImpl;
import com.digipals.wms.payment.service.IncomingPaymentServiceImpl;
import com.digipals.wms.salesorder.dto.CreateSalesOrderItemRequest;
import com.digipals.wms.salesorder.dto.CreateSalesOrderRequest;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import com.digipals.wms.salesorder.sap.SapSalesOrderClient;
import com.digipals.wms.salesorder.sap.SapSalesOrderResponse;
import com.digipals.wms.salesorder.service.SalesOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class O2CEndToEndWorkflowTest {

    private SalesOrderRepository salesOrderRepository;
    private OutboundDeliveryRepository deliveryRepository;
    private BillingDocumentRepository billingRepository;
    private IncomingPaymentRepository paymentRepository;
    private PaymentAllocationRepository allocationRepository;
    private DunningCaseRepository dunningRepository;
    private SapSalesOrderClient sapClient;

    private SalesOrderServiceImpl salesOrderService;
    private OutboundDeliveryServiceImpl deliveryService;
    private BillingDocumentServiceImpl billingService;
    private IncomingPaymentServiceImpl paymentService;
    private CashApplicationServiceImpl cashApplicationService;
    private DunningServiceImpl dunningService;
    private DocumentFlowServiceImpl documentFlowService;

    private final List<IncomingPayment> persistedPayments = new ArrayList<>();
    private final List<DunningCase> persistedDunningCases = new ArrayList<>();

    @BeforeEach
    void setUp() {
        salesOrderRepository = mock(SalesOrderRepository.class);
        deliveryRepository = mock(OutboundDeliveryRepository.class);
        billingRepository = mock(BillingDocumentRepository.class);
        paymentRepository = mock(IncomingPaymentRepository.class);
        allocationRepository = mock(PaymentAllocationRepository.class);
        dunningRepository = mock(DunningCaseRepository.class);
        sapClient = mock(SapSalesOrderClient.class);

        salesOrderService = new SalesOrderServiceImpl(salesOrderRepository, sapClient);
        ReflectionTestUtils.setField(salesOrderService, "sapIntegrationEnabled", true);

        deliveryService = new OutboundDeliveryServiceImpl(deliveryRepository, salesOrderRepository);
        billingService = new BillingDocumentServiceImpl(billingRepository, deliveryRepository);
        paymentService = new IncomingPaymentServiceImpl(paymentRepository, allocationRepository, billingRepository);
        cashApplicationService = new CashApplicationServiceImpl(paymentRepository, allocationRepository, billingRepository);
        dunningService = new DunningServiceImpl(dunningRepository, billingRepository, allocationRepository);
        documentFlowService = new DocumentFlowServiceImpl(
                salesOrderRepository,
                deliveryRepository,
                billingRepository,
                allocationRepository,
                dunningRepository);

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deliveryRepository.save(any(OutboundDelivery.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(billingRepository.save(any(BillingDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(IncomingPayment.class))).thenAnswer(invocation -> {
            IncomingPayment payment = invocation.getArgument(0);
            if (!persistedPayments.contains(payment)) {
                persistedPayments.add(payment);
            }
            return payment;
        });
        when(dunningRepository.save(any(DunningCase.class))).thenAnswer(invocation -> {
            DunningCase dunning = invocation.getArgument(0);
            if (!persistedDunningCases.contains(dunning)) {
                persistedDunningCases.add(dunning);
            }
            return dunning;
        });

        when(sapClient.createSalesOrder(any())).thenReturn(new SapSalesOrderResponse(null, "SAP-SO-10001"));

        when(deliveryRepository.existsBySalesOrderId(any())).thenReturn(false);
        when(billingRepository.findByOutboundDeliveryId(any())).thenReturn(Optional.empty());
        when(dunningRepository.findByBillingDocumentIdAndStatusNot(any(), any())).thenReturn(Optional.empty());

        when(allocationRepository.findActiveByBillingDocumentId(any(), any()))
                .thenAnswer(invocation -> activeAllocationsForBilling(invocation.getArgument(0)));
        when(allocationRepository.sumActiveAmountByPaymentId(any(), any()))
                .thenAnswer(invocation -> sumActiveAmountForPayment(invocation.getArgument(0)));
    }

    @Test
    void completesFullO2CFlowFromSalesOrderToDunningResolution() {
        // 1. Sales Order -> SAP CREATED
        SalesOrder salesOrder = salesOrderService.create(new CreateSalesOrderRequest(
                "CUST-100",
                "1000",
                "10",
                "00",
                "O2C integration test",
                List.of(new CreateSalesOrderItemRequest("MAT-001", new BigDecimal("10"), new BigDecimal("100")))
        ));

        UUID salesOrderId = UUID.randomUUID();
        salesOrder.setId(salesOrderId);
        salesOrder.setStatus(SalesOrderStatus.CREATED);
        when(salesOrderRepository.findById(salesOrderId)).thenReturn(Optional.of(salesOrder));

        assertThat(salesOrder.getStatus()).isEqualTo(SalesOrderStatus.CREATED);
        assertThat(salesOrder.getSapOrderNumber()).isEqualTo("SAP-SO-10001");
        assertThat(salesOrder.getTotalAmount()).isEqualByComparingTo("1000.00");

        // 2. Outbound Delivery -> Picking -> Packing -> PGI
        OutboundDelivery delivery = deliveryService.create(new CreateOutboundDeliveryRequest(
                salesOrderId, "SP-01", LocalDateTime.now().plusDays(1)));
        UUID deliveryId = UUID.randomUUID();
        delivery.setId(deliveryId);
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.OPEN);
        deliveryService.startPicking(deliveryId);
        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.PICKING);
        deliveryService.confirmPicking(deliveryId);
        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.PICKED);
        deliveryService.confirmPacking(deliveryId);
        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.PACKED);
        deliveryService.postGoodsIssue(deliveryId);
        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.POSTED_GOODS_ISSUE);
        assertThat(delivery.getItems().get(0).getDeliveredQuantity()).isEqualByComparingTo("10");

        // 3. Billing -> POSTED
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        BillingDocument billing = billingService.create(new CreateBillingRequest(deliveryId, "USD", null));
        UUID billingId = UUID.randomUUID();
        billing.setId(billingId);
        when(billingRepository.findById(billingId)).thenReturn(Optional.of(billing));
        when(billingRepository.findByOutboundDeliveryId(deliveryId)).thenReturn(Optional.of(billing));

        assertThat(billing.getStatus()).isEqualTo(BillingStatus.DRAFT);
        assertThat(billing.getTotalAmount()).isEqualByComparingTo("1000.00");
        billingService.post(billingId);
        assertThat(billing.getStatus()).isEqualTo(BillingStatus.POSTED);

        // 4. First payment partially settles the invoice.
        IncomingPayment firstPayment = paymentService.receive(new CreateIncomingPaymentRequest(
                billingId, new BigDecimal("400.00"), "USD", "BANK-001"));
        UUID firstPaymentId = UUID.randomUUID();
        firstPayment.setId(firstPaymentId);
        assertThat(firstPayment.getStatus()).isEqualTo(PaymentStatus.FULLY_APPLIED);
        assertThat(sumActiveAmountForBilling(billingId)).isEqualByComparingTo("400.00");
        assertThat(billing.getTotalAmount().subtract(sumActiveAmountForBilling(billingId)))
                .isEqualByComparingTo("600.00");

        // 5. Simulate a second unapplied receipt, then explicitly cash-apply it.
        IncomingPayment finalPayment = IncomingPayment.builder()
                .paymentNumber("PAY-E2E-002")
                .customerCode(billing.getCustomerCode())
                .amount(new BigDecimal("600.00"))
                .currency("USD")
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PARTIALLY_APPLIED)
                .build();
        UUID finalPaymentId = UUID.randomUUID();
        finalPayment.setId(finalPaymentId);
        persistedPayments.add(finalPayment);
        when(paymentRepository.findById(finalPaymentId)).thenReturn(Optional.of(finalPayment));

        // 6. Move the invoice into an overdue state before creating dunning.
        billing.setDueDate(LocalDateTime.now().minusDays(1));

        DunningCase dunning = dunningService.create(new CreateDunningRequest(
                billingId, 1, "Outstanding O2C balance"));
        UUID dunningId = UUID.randomUUID();
        dunning.setId(dunningId);
        when(dunningRepository.findById(dunningId)).thenReturn(Optional.of(dunning));

        assertThat(dunning.getStatus()).isEqualTo(DunningStatus.OPEN);
        assertThat(dunning.getOutstandingAmount()).isEqualByComparingTo("600.00");

        dunningService.send(dunningId);
        assertThat(dunning.getStatus()).isEqualTo(DunningStatus.SENT);

        // 7. Final cash application clears the remaining balance.
        cashApplicationService.apply(new CashApplicationRequest(
                finalPaymentId, billingId, new BigDecimal("600.00")));

        assertThat(sumActiveAmountForBilling(billingId)).isEqualByComparingTo("1000.00");
        assertThat(finalPayment.getStatus()).isEqualTo(PaymentStatus.FULLY_APPLIED);

        // 8. Dunning can only resolve after the invoice is fully settled.
        dunningService.resolve(dunningId);
        assertThat(dunning.getStatus()).isEqualTo(DunningStatus.RESOLVED);
        assertThat(dunning.getOutstandingAmount()).isEqualByComparingTo("0.00");

        // 9. Document flow exposes the complete O2C chain.
        when(deliveryRepository.findBySalesOrderIdOrderByCreatedAtDesc(salesOrderId))
                .thenReturn(List.of(delivery));
        when(dunningRepository.findByBillingDocumentIdAndStatusNot(billingId, DunningStatus.CANCELLED))
                .thenReturn(Optional.of(dunning));

        DocumentFlowResponse flow = documentFlowService.getBySalesOrderId(salesOrderId);

        assertThat(flow.getRootDocumentType()).isEqualTo("SALES_ORDER");
        assertThat(flow.getRootDocumentId()).isEqualTo(salesOrderId);
        assertThat(flow.getFlow())
                .extracting("documentType")
                .containsExactly(
                        "SALES_ORDER",
                        "OUTBOUND_DELIVERY",
                        "BILLING_DOCUMENT",
                        "INCOMING_PAYMENT",
                        "INCOMING_PAYMENT",
                        "DUNNING_CASE"
                );
    }

    @Test
    void blocksBillingBeforePostGoodsIssue() {
        SalesOrder salesOrder = SalesOrder.builder()
                .orderNumber("SO-NEG-001")
                .customerCode("CUST-100")
                .salesOrganization("1000")
                .distributionChannel("10")
                .division("00")
                .status(SalesOrderStatus.CREATED)
                .build();
        salesOrder.setId(UUID.randomUUID());
        salesOrder.addItem(com.digipals.wms.salesorder.entity.SalesOrderItem.builder()
                .itemNumber(10)
                .materialCode("MAT-001")
                .quantity(new BigDecimal("1"))
                .unitPrice(new BigDecimal("100"))
                .netValue(new BigDecimal("100"))
                .build());

        OutboundDelivery delivery = deliveryService.create(new CreateOutboundDeliveryRequest(
                salesOrder.getId(), "SP-01", null));
        UUID deliveryId = UUID.randomUUID();
        delivery.setId(deliveryId);
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> billingService.create(new CreateBillingRequest(deliveryId, "USD", null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Post Goods Issue");
    }

    private List<PaymentAllocation> activeAllocationsForBilling(UUID billingId) {
        return persistedPayments.stream()
                .flatMap(payment -> payment.getAllocations() == null
                        ? java.util.stream.Stream.<PaymentAllocation>empty()
                        : payment.getAllocations().stream())
                .filter(allocation -> allocation.getBillingDocument() != null
                        && billingId.equals(allocation.getBillingDocument().getId()))
                .toList();
    }

    private BigDecimal sumActiveAmountForBilling(UUID billingId) {
        return activeAllocationsForBilling(billingId).stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumActiveAmountForPayment(UUID paymentId) {
        return persistedPayments.stream()
                .filter(payment -> paymentId.equals(payment.getId()))
                .flatMap(payment -> payment.getAllocations() == null
                        ? java.util.stream.Stream.<PaymentAllocation>empty()
                        : payment.getAllocations().stream())
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
