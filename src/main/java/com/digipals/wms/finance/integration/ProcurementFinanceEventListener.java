package com.digipals.wms.finance.integration;

import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.integration.procurement.GoodsReceiptApprovedEvent;
import com.digipals.wms.integration.procurement.VendorInvoiceMatchedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Finance-side integration adapter.
 *
 * Procurement publishes business events; Finance consumes the contracts and
 * creates its own accounting representation. No Procurement entity references
 * a Finance entity, and Finance does not depend on Procurement persistence.
 */
@Component
@RequiredArgsConstructor
public class ProcurementFinanceEventListener {

    private final FinancePostingService financePostingService;

    @EventListener
    @Transactional
    public void onGoodsReceiptApproved(GoodsReceiptApprovedEvent event) {
        financePostingService.postGoodsReceipt(
                event.sourceDocumentId(),
                event.sourceDocumentNumber(),
                event.currency(),
                event.amount()
        );
    }

    @EventListener
    @Transactional
    public void onVendorInvoiceMatched(VendorInvoiceMatchedEvent event) {
        financePostingService.postVendorInvoice(
                event.sourceDocumentId(),
                event.sourceDocumentNumber(),
                event.currency(),
                event.amount()
        );
    }
}
