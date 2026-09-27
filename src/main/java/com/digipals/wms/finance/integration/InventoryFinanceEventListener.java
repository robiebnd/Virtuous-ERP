package com.digipals.wms.finance.integration;

import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.integration.inventory.GoodsIssuePostedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Finance-side adapter for Inventory/O2C integration.
 *
 * Inventory owns stock movements; Finance owns the accounting document.
 */
@Component
@RequiredArgsConstructor
public class InventoryFinanceEventListener {

    private final FinancePostingService financePostingService;

    @EventListener
    @Transactional
    public void onGoodsIssuePosted(GoodsIssuePostedEvent event) {
        financePostingService.postGoodsIssueWithCogs(
                event.sourceDocumentId(),
                event.sourceDocumentNumber(),
                event.currency(),
                event.cogsAmount()
        );
    }
}
