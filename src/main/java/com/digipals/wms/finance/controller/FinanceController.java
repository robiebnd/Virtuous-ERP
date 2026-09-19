package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.AccountingDocumentResponse;
import com.digipals.wms.finance.dto.GlAccountResponse;
import com.digipals.wms.finance.dto.InventoryGlReconciliationResponse;
import com.digipals.wms.finance.dto.InventoryValuationResponse;
import com.digipals.wms.finance.dto.JournalEntryRequest;
import com.digipals.wms.finance.dto.OpenItemResponse;
import com.digipals.wms.finance.dto.TrialBalanceLine;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.finance.repository.GlAccountRepository;
import com.digipals.wms.finance.service.FinanceQueryService;
import com.digipals.wms.finance.service.InventoryGlReconciliationService;
import com.digipals.wms.finance.service.InventoryValuationService;
import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.finance.service.OpenItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {
    private final GlAccountRepository glAccountRepository;
    private final AccountingDocumentRepository accountingDocumentRepository;
    private final FinanceQueryService financeQueryService;
    private final OpenItemService openItemService;
    private final InventoryValuationService inventoryValuationService;
    private final InventoryGlReconciliationService inventoryGlReconciliationService;
    private final FinancePostingService financePostingService;

    @GetMapping("/gl-accounts")
    public List<GlAccountResponse> accounts() { return glAccountRepository.findAllByActiveTrueOrderByAccountCode().stream().map(GlAccountResponse::from).toList(); }

    @GetMapping("/accounting-documents")
    public List<AccountingDocumentResponse> documents() { return accountingDocumentRepository.findAllByOrderByPostingDateDesc().stream().map(AccountingDocumentResponse::from).toList(); }

    @GetMapping("/accounting-documents/{id}")
    public AccountingDocumentResponse document(@PathVariable UUID id) { return AccountingDocumentResponse.from(accountingDocumentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Accounting document not found: " + id))); }

    @GetMapping("/trial-balance")
    public List<TrialBalanceLine> trialBalance() { return financeQueryService.trialBalance(); }

    @GetMapping("/open-items/ap")
    public List<OpenItemResponse> accountsPayableOpenItems() { return openItemService.accountsPayable(); }

    @GetMapping("/open-items/ar")
    public List<OpenItemResponse> accountsReceivableOpenItems() { return openItemService.accountsReceivable(); }

    @GetMapping("/ageing")
    public OpenItemService.AgeingSummary ageing(@RequestParam(defaultValue = "AR") String type) { return openItemService.ageing(type); }

    @GetMapping("/inventory-valuation")
    public List<InventoryValuationResponse> inventoryValuation(@RequestParam(required = false) UUID warehouseId) {
        return warehouseId == null
                ? inventoryValuationService.valuation()
                : inventoryValuationService.valuationByWarehouse(warehouseId);
    }

    @GetMapping("/inventory-reconciliation")
    public InventoryGlReconciliationResponse inventoryReconciliation() {
        return inventoryGlReconciliationService.reconcile();
    }
    @PostMapping("/journal-entries")
    public AccountingDocumentResponse postJournalEntry(@Valid @RequestBody JournalEntryRequest request) {
        var postings = request.lines().stream()
                .map(line -> new FinancePostingService.PostingLine(
                        line.accountCode(),
                        line.debit(),
                        line.credit(),
                        line.costCenter(),
                        line.profitCenter(),
                        line.functionalArea(),
                        line.segment(),
                        line.lineText(),
                        line.internalOrderCode(),
                        line.wbsElement()))
                .toList();
        var document = financePostingService.postBalanced(
                request.documentType(),
                "MANUAL_JOURNAL",
                null,
                request.referenceNumber(),
                request.currency(),
                request.description(),
                postings);
        return AccountingDocumentResponse.from(document);
    }

}
