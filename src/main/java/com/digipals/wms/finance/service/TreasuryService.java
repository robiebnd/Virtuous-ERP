package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TreasuryService {
    private final BankAccountRepository bankAccounts;
    private final BankTransactionRepository transactions;
    private final LiquidityForecastRepository forecasts;
    private final AccountingDocumentRepository documents;
    private final FinanceQueryService financeQueryService;

    public BankTransaction captureStatementLine(BankTransactionRequest request) {
        BankAccount account = bankAccounts.findByAccountNumber(request.bankAccountNumber().trim())
                .filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active bank account not found: " + request.bankAccountNumber()));
        String direction = request.direction().trim().toUpperCase(Locale.ROOT);
        if (!direction.equals("IN") && !direction.equals("OUT")) throw new InvalidWorkflowException("Bank transaction direction must be IN or OUT.");
        if (transactions.findByBankAccountIdAndTransactionNumber(account.getId(), request.transactionNumber().trim()).isPresent())
            throw new InvalidWorkflowException("Bank statement transaction already exists: " + request.transactionNumber());
        if (request.valueDate() != null && request.valueDate().isBefore(request.transactionDate()))
            throw new InvalidWorkflowException("Value date cannot be before transaction date.");
        return transactions.save(BankTransaction.builder().bankAccount(account).transactionNumber(request.transactionNumber().trim())
                .transactionDate(request.transactionDate()).valueDate(request.valueDate()).amount(money(request.amount()))
                .direction(direction).reference(request.reference()).description(request.description()).status("UNRECONCILED").active(true).build());
    }

    public BankTransaction reconcile(BankReconciliationRequest request) {
        BankAccount account = bankAccounts.findByAccountNumber(request.bankAccountNumber().trim())
                .orElseThrow(() -> new InvalidWorkflowException("Bank account not found: " + request.bankAccountNumber()));
        BankTransaction transaction = transactions.findByBankAccountIdAndTransactionNumber(account.getId(), request.transactionNumber().trim())
                .orElseThrow(() -> new InvalidWorkflowException("Bank transaction not found: " + request.transactionNumber()));
        if ("RECONCILED".equals(transaction.getStatus())) throw new InvalidWorkflowException("Bank transaction is already reconciled.");
        AccountingDocument document = documents.findByDocumentNumber(request.accountingDocumentNumber().trim())
                .filter(x -> "POSTED".equals(x.getStatus()))
                .orElseThrow(() -> new InvalidWorkflowException("Posted accounting document not found: " + request.accountingDocumentNumber()));
        BigDecimal bankDebit = document.getLines().stream().filter(l -> l.getGlAccount().getAccountCode().equals(account.getGlAccountCode()))
                .map(AccountingLine::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bankCredit = document.getLines().stream().filter(l -> l.getGlAccount().getAccountCode().equals(account.getGlAccountCode()))
                .map(AccountingLine::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expected = "IN".equals(transaction.getDirection()) ? bankDebit : bankCredit;
        if (expected.compareTo(transaction.getAmount()) != 0)
            throw new InvalidWorkflowException("Bank transaction amount does not match the selected accounting document bank line.");
        transaction.setAccountingDocumentId(document.getId());
        transaction.setStatus("RECONCILED");
        return transactions.save(transaction);
    }

    @Transactional(readOnly=true)
    public List<BankTransaction> transactions(String bankAccountNumber) {
        if (bankAccountNumber == null || bankAccountNumber.isBlank()) return transactions.findAll().stream()
                .sorted(Comparator.comparing(BankTransaction::getTransactionDate).reversed()).toList();
        BankAccount account=bankAccounts.findByAccountNumber(bankAccountNumber.trim()).orElseThrow(() -> new InvalidWorkflowException("Bank account not found: "+bankAccountNumber));
        return transactions.findByBankAccountIdOrderByTransactionDateDesc(account.getId());
    }

    @Transactional(readOnly=true)
    public Map<String,Object> cashPosition() {
        Map<String,Map<String,Object>> byCode=new LinkedHashMap<>();
        Map<String, BigDecimal> balances=new HashMap<>();
        financeQueryService.trialBalance().forEach(x -> balances.put(x.accountCode(), x.debitBalance().subtract(x.creditBalance()));
        );
        BigDecimal total=BigDecimal.ZERO;
        for(BankAccount b:bankAccounts.findByActiveTrueOrderByBankNameAsc()){
            BigDecimal balance=balances.getOrDefault(b.getGlAccountCode(),BigDecimal.ZERO);
            byCode.put(b.getAccountNumber(),Map.of("bankName",b.getBankName(),"currency",b.getCurrency(),"glAccountCode",b.getGlAccountCode(),"balance",balance));
            total=total.add(balance);
        }
        return Map.of("companyCode","ZW01","totalCash",total.setScale(2,RoundingMode.HALF_UP),"accounts",byCode);
    }

    public LiquidityForecast createForecast(LiquidityForecastRequest request) {
        BigDecimal inflow=money(request.expectedInflow()), outflow=money(request.expectedOutflow());
        if(inflow.compareTo(BigDecimal.ZERO)==0 && outflow.compareTo(BigDecimal.ZERO)==0) throw new InvalidWorkflowException("Liquidity forecast must contain an inflow or outflow.");
        String currency=request.currency()==null||request.currency().isBlank()?"USD":request.currency().trim().toUpperCase(Locale.ROOT);
        if(!currency.matches("[A-Z]{3}")) throw new InvalidWorkflowException("Currency must be a 3-letter ISO code.");
        return forecasts.save(LiquidityForecast.builder().forecastDate(request.forecastDate()).category(request.category().trim())
                .description(request.description()).expectedInflow(inflow).expectedOutflow(outflow).currency(currency).status("OPEN").active(true).build());
    }

    @Transactional(readOnly=true)
    public List<LiquidityForecast> forecasts(LocalDate from,LocalDate to){
        LocalDate start=from==null?LocalDate.now():from, end=to==null?start.plusDays(30):to;
        if(end.isBefore(start)) throw new InvalidWorkflowException("Forecast end date cannot be before start date.");
        return forecasts.findByForecastDateBetweenAndStatusNotOrderByForecastDateAsc(start,end,"CANCELLED");
    }

    private BigDecimal money(BigDecimal v){if(v==null||v.compareTo(BigDecimal.ZERO)<0)throw new InvalidWorkflowException("Amount must be zero or greater.");return v.setScale(2,RoundingMode.HALF_UP);}
}