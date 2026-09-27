package com.digipals.wms.integration.outbox;

import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.integration.IntegrationEventTypes;
import com.digipals.wms.integration.inventory.GoodsIssuePostedEvent;
import com.digipals.wms.integration.procurement.GoodsReceiptApprovedEvent;
import com.digipals.wms.integration.procurement.VendorInvoiceMatchedEvent;
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
                case IntegrationEventTypes.INVENTORY_GOODS_ISSUE_POSTED -> {
                    GoodsIssuePostedEvent payload = objectMapper.readValue(event.getPayload(), GoodsIssuePostedEvent.class);
                    financePostingService.postGoodsIssueWithCogs(
                            payload.sourceDocumentId(), payload.sourceDocumentNumber(), payload.currency(), payload.cogsAmount());
                }
                default -> throw new IllegalArgumentException("Unsupported integration outbox event type: " + event.getEventType());
            }
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to deserialize integration outbox event " + event.getEventType() + ".", ex);
        }
    }
}
