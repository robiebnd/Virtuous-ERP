package com.digipals.wms.integration.outbox;

import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.integration.IntegrationEventTypes;
import com.digipals.wms.integration.inventory.GoodsIssuePostedEvent;
import com.digipals.wms.integration.inventory.StockAdjustmentPostedEvent;
import com.digipals.wms.integration.o2c.CashApplicationPostedEvent;
import com.digipals.wms.integration.o2c.CustomerBillingPostedEvent;
import com.digipals.wms.integration.o2c.IncomingPaymentCancelledEvent;
import com.digipals.wms.integration.o2c.IncomingPaymentReceivedEvent;
import com.digipals.wms.integration.manufacturing.ProductionConfirmedEvent;
import com.digipals.wms.integration.procurement.GoodsReceiptApprovedEvent;
import com.digipals.wms.integration.procurement.VendorInvoiceMatchedEvent;
import com.digipals.wms.integration.procurement.VendorPaymentPostedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceIntegrationOutboxHandler {

    private final FinancePostingService financePostingService;
    private final ObjectMapper objectMapper;

    public void handle(IntegrationOutboxEvent event) {
        try {
            switch (event.getEventType()) {
                case IntegrationEventTypes.PROCUREMENT_GOODS_RECEIPT_APPROVED -> {
                    GoodsReceiptApprovedEvent payload = objectMapper.readValue(event.getPayload(), GoodsReceiptApprovedEvent.class);
                    financePostingService.postGoodsReceipt(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.amount());
                }
                case IntegrationEventTypes.PROCUREMENT_VENDOR_INVOICE_MATCHED -> {
                    VendorInvoiceMatchedEvent payload = objectMapper.readValue(event.getPayload(), VendorInvoiceMatchedEvent.class);
                    financePostingService.postVendorInvoice(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.amount());
                }
                case IntegrationEventTypes.PROCUREMENT_VENDOR_PAYMENT_POSTED -> {
                    VendorPaymentPostedEvent payload = objectMapper.readValue(event.getPayload(), VendorPaymentPostedEvent.class);
                    financePostingService.postVendorPayment(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.amount());
                }
                case IntegrationEventTypes.INVENTORY_GOODS_ISSUE_POSTED -> {
                    GoodsIssuePostedEvent payload = objectMapper.readValue(event.getPayload(), GoodsIssuePostedEvent.class);
                    financePostingService.postGoodsIssueWithCogs(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.cogsAmount());
                }
                case IntegrationEventTypes.INVENTORY_STOCK_ADJUSTMENT_POSTED -> {
                    StockAdjustmentPostedEvent payload = objectMapper.readValue(event.getPayload(), StockAdjustmentPostedEvent.class);
                    financePostingService.postStockAdjustment(
                            payload.sourceDocumentId(),
                            payload.sourceDocumentNumber(),
                            payload.currency(),
                            payload.inventoryIncreaseAmount(),
                            payload.inventoryDecreaseAmount());
                }
                case IntegrationEventTypes.O2C_CUSTOMER_BILLING_POSTED -> {
                    CustomerBillingPostedEvent payload = objectMapper.readValue(event.getPayload(), CustomerBillingPostedEvent.class);
                    financePostingService.postCustomerInvoice(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.revenueAmount());
                }
                case IntegrationEventTypes.O2C_INCOMING_PAYMENT_RECEIVED -> {
                    IncomingPaymentReceivedEvent payload = objectMapper.readValue(event.getPayload(), IncomingPaymentReceivedEvent.class);
                    financePostingService.postIncomingPayment(
                            payload.sourceDocumentId(),
                            payload.sourceDocumentNumber(),
                            payload.currency(),
                            payload.paymentAmount(),
                            payload.appliedAmount());
                }
                case IntegrationEventTypes.O2C_INCOMING_PAYMENT_CANCELLED -> {
                    IncomingPaymentCancelledEvent payload = objectMapper.readValue(event.getPayload(), IncomingPaymentCancelledEvent.class);
                    financePostingService.reverseIncomingPayment(
                            payload.sourceDocumentId(),
                            payload.sourceDocumentNumber(),
                            payload.currency(),
                            payload.paymentAmount(),
                            payload.appliedAmount());
                }
                case IntegrationEventTypes.O2C_CASH_APPLICATION_POSTED -> {
                    CashApplicationPostedEvent payload = objectMapper.readValue(event.getPayload(), CashApplicationPostedEvent.class);
                    financePostingService.postCashApplication(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.amount());
                }\n                case IntegrationEventTypes.MANUFACTURING_PRODUCTION_CONFIRMED -> {
                    ProductionConfirmedEvent payload = objectMapper.readValue(event.getPayload(), ProductionConfirmedEvent.class);
                    financePostingService.postProductionReceipt(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.valuationAmount());
                }
                default -> throw new IllegalArgumentException("Unsupported integration outbox event type: " + event.getEventType());
            }
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to deserialize integration outbox event " + event.getEventType() + ".", ex);
        }
    }
}
