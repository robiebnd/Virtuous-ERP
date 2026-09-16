package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.AccountingDocumentResponse;
import com.digipals.wms.finance.dto.GlAccountResponse;
import com.digipals.wms.finance.dto.TrialBalanceLine;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.finance.repository.GlAccountRepository;
import com.digipals.wms.finance.service.FinanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {
    private final GlAccountRepository glAccountRepository;
    private final AccountingDocumentRepository accountingDocumentRepository;
    private final FinanceQueryService financeQueryService;

    @GetMapping("/gl-accounts")
    public List<GlAccountResponse> accounts() { return glAccountRepository.findAllByActiveTrueOrderByAccountCode().stream().map(GlAccountResponse::from).toList(); }

    @GetMapping("/accounting-documents")
    public List<AccountingDocumentResponse> documents() { return accountingDocumentRepository.findAllByOrderByPostingDateDesc().stream().map(AccountingDocumentResponse::from).toList(); }

    @GetMapping("/accounting-documents/{id}")
    public AccountingDocumentResponse document(@PathVariable UUID id) { return AccountingDocumentResponse.from(accountingDocumentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Accounting document not found: " + id))); }

    @GetMapping("/trial-balance")
    public List<TrialBalanceLine> trialBalance() { return financeQueryService.trialBalance(); }
}
