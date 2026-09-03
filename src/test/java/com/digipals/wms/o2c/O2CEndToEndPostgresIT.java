package com.digipals.wms.o2c;

import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.billing.service.BillingDocumentService;
import com.digipals.wms.dunning.dto.CreateDunningRequest;
import com.digipals.wms.dunning.entity.DunningCase;
import com.digipals.wms.dunning.entity.DunningStatus;
import com.digipals.wms.dunning.repository.DunningCaseRepository;
import com.digipals.wms.dunning.service.DunningService;
import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;
import com.digipals.wms.outbounddelivery.repository.OutboundDeliveryRepository;
import com.digipals.wms.outbounddelivery.service.OutboundDeliveryService;
import com.digipals.wms.payment.dto.CashApplicationRequest;
import com.digipals.wms.payment.dto.CreateIncomingPaymentRequest;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.repository.IncomingPaymentRepository;
import com.digipals.wms.payment.service.CashApplicationService;
import com.digipals.wms.payment.service.IncomingPaymentService;
import com.digipals.wms.salesorder.dto.CreateSalesOrderItemRequest;
import com.digipals.wms.salesorder.dto.CreateSalesOrderRequest;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import com.digipals.wms.salesorder.service.SalesOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class O2CEndToEndPostgresIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("virtuous_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("sap.sales-order.enabled", () -> false);
    }

    @Autowired SalesOrderService salesOrderService;
    @Autowired OutboundDeliveryService deliveryService;
    @Autowired BillingDocumentService billingService;
    @Autowired IncomingPaymentService paymentService;
    @Autowired CashApplicationService cashApplicationService;
    @Autowired DunningService dunningService;
    @Autowired SalesOrderRepository salesOrderRepository;
    @Autowired OutboundDeliveryRepository deliveryRepository;
    @Autowired BillingDocumentRepository billingRepository;
    @Autowired IncomingPaymentRepository paymentRepository;
    @Autowired DunningCaseRepository dunningRepository;

    @Test
    void completesO2CFlowAgainstRealPostgres() {
        SalesOrder order = salesOrderService.create(new CreateSalesOrderRequest(
                "IT-CUST-001", "1000", "10", "00", "PostgreSQL O2C test",
                List.of(new CreateSalesOrderItemRequest("IT-MAT-001", new BigDecimal("10"), new BigDecimal("100")))
        ));

        assertThat(order.getStatus()).isEqualTo(SalesOrderStatus.DRAFT);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("1000.00");
        assertThat(salesOrderRepository.findById(order.getId())).isPresent();

        // The current production flow requires a CREATED order for delivery.
        order.setStatus(SalesOrderStatus.CREATED);
        salesOrderRepository.save(order);

        OutboundDelivery delivery = deliveryService.create(new CreateOutboundDeliveryRequest(
                order.getId(), "SP-01", LocalDateTime.now().plusDays(1)));
        assertThat(delivery.getStatus()).isEqualTo(OutboundDeliveryStatus.OPEN);

        deliveryService.startPicking(delivery.getId());
        deliveryService.confirmPicking(delivery.getId());
        deliveryService.confirmPacking(delivery.getId());
        deliveryService.postGoodsIssue(delivery.getId());

        OutboundDelivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertThat(persistedDelivery.getStatus()).isEqualTo(OutboundDeliveryStatus.POSTED_GOODS_ISSUE);
        assertThat(persistedDelivery.getItems().get(0).getDeliveredQuantity()).isEqualByComparingTo("10");

        BillingDocument billing = billingService.create(
                new CreateBillingRequest(delivery.getId(), "USD", LocalDateTime.now().minusDays(1)));
        billingService.post(billing.getId());

        BillingDocument persistedBilling = billingRepository.findById(billing.getId()).orElseThrow();
        assertThat(persistedBilling.getStatus()).isEqualTo(BillingStatus.POSTED);
        assertThat(persistedBilling.getTotalAmount()).isEqualByComparingTo("1000.00");

        IncomingPayment firstPayment = paymentService.receive(
                new CreateIncomingPaymentRequest(billing.getId(), new BigDecimal("400.00"), "USD", "IT-PAY-001"));
        IncomingPayment persistedFirstPayment = paymentRepository.findById(firstPayment.getId()).orElseThrow();
        assertThat(persistedFirstPayment.getStatus()).isEqualTo(PaymentStatus.FULLY_APPLIED);

        DunningCase dunning = dunningService.create(
                new CreateDunningRequest(billing.getId(), 1, "Outstanding balance"));
        assertThat(dunning.getStatus()).isEqualTo(DunningStatus.OPEN);
        assertThat(dunning.getOutstandingAmount()).isEqualByComparingTo("600.00");

        dunningService.send(dunning.getId());

        IncomingPayment finalPayment = paymentService.receive(
                new CreateIncomingPaymentRequest(billing.getId(), new BigDecimal("600.00"), "USD", "IT-PAY-002"));
        assertThat(finalPayment.getStatus()).isEqualTo(PaymentStatus.FULLY_APPLIED);

        BillingDocument settledBilling = billingRepository.findById(billing.getId()).orElseThrow();
        assertThat(settledBilling.getStatus()).isEqualTo(BillingStatus.POSTED);

        DunningCase resolved = dunningService.resolve(dunning.getId());
        assertThat(resolved.getStatus()).isEqualTo(DunningStatus.RESOLVED);
        assertThat(resolved.getOutstandingAmount()).isEqualByComparingTo("0.00");

        assertThat(deliveryRepository.findById(delivery.getId())).isPresent();
        assertThat(billingRepository.findByOutboundDeliveryId(delivery.getId())).isPresent();
        assertThat(paymentRepository.findById(firstPayment.getId())).isPresent();
        assertThat(paymentRepository.findById(finalPayment.getId())).isPresent();
        assertThat(dunningRepository.findById(dunning.getId())).isPresent();
    }
}
