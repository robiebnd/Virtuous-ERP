package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.nio.charset.StandardCharsets;
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
    private final TreasuryInstrumentRepository instruments;
    private final TreasuryRiskLimitRepository riskLimits;
    private final FinancePostingService postingService;

    public TreasuryRiskLimit saveRiskLimit(TreasuryRiskLimitRequest r) {
        String code = r.limitCode().trim();
        TreasuryRiskLimit x = riskLimits.findByLimitCode(code).orElseGet(TreasuryRiskLimit::new);
        x.setLimitCode(code);
        x.setCounterparty(r.counterparty() == null ? null : r.counterparty().trim());
        x.setCurrency(r.currency() == null ? null : currency(r.currency()));
        x.setInstrumentType(r.instrumentType() == null ? null : r.instrumentType().trim().toUpperCase(Locale.ROOT));
        x.setLimitAmount(money(r.limitAmount()));
        x.setActive(true);
        return riskLimits.save(x);
    }

    public List<TreasuryRiskLimit> riskLimits() {
        return riskLimits.findAll();
    }

    public TreasuryInstrument createInstrument(TreasuryInstrumentRequest r) {
        String instrumentNumber = r.instrumentNumber().trim();
        String type = r.instrumentType().trim().toUpperCase(Locale.ROOT);
        String counterparty = r.counterparty().trim();
        String instrumentCurrency = currency(r.currency());

        if (instruments.findByInstrumentNumber(instrumentNumber).isPresent())
            throw new InvalidWorkflowException("Treasury instrument already exists.");
        if (r.maturityDate() != null && r.maturityDate().isBefore(r.tradeDate()))
            throw new InvalidWorkflowException("Maturity date cannot be before trade date.");
        BigDecimal notional = money(r.notionalAmount());
        BigDecimal exposure = instruments.findByStatusOrderByMaturityDateAsc("OPEN").stream()
                .filter(x -> x.getCounterparty().equalsIgnoreCase(counterparty)
                        && x.getCurrency().equalsIgnoreCase(instrumentCurrency)
                        && x.getInstrumentType().equalsIgnoreCase(type))
                .map(TreasuryInstrument::getNotionalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(notional);

        riskLimits.findAll().stream()
                .filter(x -> x.isActive()
                        && x.getCounterparty() != null
                        && x.getCounterparty().equalsIgnoreCase(counterparty)
                        && (x.getInstrumentType() == null || x.getInstrumentType().equalsIgnoreCase(type))
                        && (x.getCurrency() == null || x.getCurrency().equalsIgnoreCase(instrumentCurrency)))
                .forEach(limit -> {
                    if (exposure.compareTo(limit.getLimitAmount()) > 0)
                        throw new InvalidWorkflowException("Treasury risk limit exceeded: " + limit.getLimitCode());
                });

        return instruments.save(TreasuryInstrument.builder()
                .instrumentNumber(instrumentNumber)
                .instrumentType(type)
                .counterparty(counterparty)
                .currency(instrumentCurrency)
                .notionalAmount(notional)
                .tradeDate(r.tradeDate())
                .maturityDate(r.maturityDate())
                .status("OPEN")
                .valuationAmount(BigDecimal.ZERO.setScale(2))
                .lastValuationDate(null)
                .hedgeDesignated(r.hedgeDesignated())
                .build());
    }

    public List<TreasuryInstrument> instruments() {
        return instruments.findByStatusOrderByMaturityDateAsc("OPEN");
    }

    public TreasuryInstrument valueInstrument(TreasuryValuationRequest r) {
        TreasuryInstrument instrument = instruments.findByInstrumentNumber(r.instrumentNumber().trim())
                .orElseThrow(() -> new InvalidWorkflowException("Treasury instrument not found: " + r.instrumentNumber()));

        if (!"OPEN".equalsIgnoreCase(instrument.getStatus()))
            throw new InvalidWorkflowException("Only open treasury instruments can be valued.");
        if (r.valuationDate().isBefore(instrument.getTradeDate()))
            throw new InvalidWorkflowException("Valuation date cannot be before trade date.");
        if (instrument.getMaturityDate() != null && r.valuationDate().isAfter(instrument.getMaturityDate()))
            throw new InvalidWorkflowException("Valuation date cannot be after instrument maturity date.");
        if (instrument.getLastValuationDate() != null && !r.valuationDate().isAfter(instrument.getLastValuationDate()))
            throw new InvalidWorkflowException("Treasury instrument valuation must be posted in chronological order.");

        String valuationCurrency = currency(r.currency() == null ? instrument.getCurrency() : r.currency());
        if (!valuationCurrency.equalsIgnoreCase(instrument.getCurrency()))
            throw new InvalidWorkflowException("Valuation currency must match the instrument currency.");

        BigDecimal newValue = r.valuationAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal previousValue = instrument.getValuationAmount() == null
                ? BigDecimal.ZERO.setScale(2)
                : instrument.getValuationAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal difference = newValue.subtract(previousValue).setScale(2, RoundingMode.HALF_UP);
        if (difference.signum() == 0)
            throw new InvalidWorkflowException("Treasury valuation difference is zero; no accounting entry is required.");

        BigDecimal absolute = difference.abs();
        String balanceAccount = r.balanceAccountCode().trim();
        String gainAccount = r.gainAccountCode().trim();
        String lossAccount = r.lossAccountCode().trim();
        UUID referenceId = UUID.nameUUIDFromBytes(
                ("TREASURY_VAL:" + instrument.getId() + ":" + r.valuationDate())
                        .getBytes(StandardCharsets.UTF_8));

        List<FinancePostingService.PostingLine> lines;
        if (difference.signum() > 0) {
            lines = List.of(
                    new FinancePostingService.PostingLine(balanceAccount, absolute, BigDecimal.ZERO, null, null, null, null, "Treasury fair value increase"),
                    new FinancePostingService.PostingLine(gainAccount, BigDecimal.ZERO, absolute, null, null, null, null, "Treasury valuation gain")
            );
        } else {
            lines = List.of(
                    new FinancePostingService.PostingLine(lossAccount, absolute, BigDecimal.ZERO, null, null, null, null, "Treasury valuation loss"),
                    new FinancePostingService.PostingLine(balanceAccount, BigDecimal.ZERO, absolute, null, null, null, null, "Treasury fair value decrease")
            );
        }

        postingService.postBalancedAtDate(
                "ZW01",
                "TREASURY_VALUATION",
                "TREASURY_INSTRUMENT",
                referenceId,
                instrument.getInstrumentNumber(),
                valuationCurrency,
                "Treasury valuation " + instrument.getInstrumentNumber() + " at " + r.valuationDate(),
                lines,
                r.valuationDate().atStartOfDay()
        );

        instrument.setValuationAmount(newValue);
        instrument.setLastValuationDate(r.valuationDate());
        return instruments.save(instrument);
    }

    public BankTransaction captureStatementLine(BankTransactionRequest request) {
        BankAccount account = bankAccounts.findByAccountNumber(request.bankAccountNumber().trim())
                .filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active bank account not found: " + request.bankAccountNumber()));
        String direction = request.direction().trim().toUpperCase(Locale.ROOT);
        if (!direction.equals("IN") && !direction.equals("OUT"))
            throw new InvalidWorkflowException("Bank transaction direction must be IN or OUT.");
        if (request.transactionDate() == null)
            throw new InvalidWorkflowException("Transaction date is required.");
        if (request.transactionNumber() == null || request.transactionNumber().isBlank())
            throw new InvalidWorkflowException("Transaction number is required.");
        if (transactions.findByBankAccountIdAndTransactionNumber(account.getId(), request.transactionNumber().trim()).isPresent())
            throw new InvalidWorkflowException("Bank statement transaction already exists: " + request.transactionNumber());
        if (request.valueDate() != null && request.valueDate().isBefore(request.transactionDate()))
            throw new InvalidWorkflowException("Value date cannot be before transaction date.");
        return transactions.save(BankTransaction.builder()
                .bankAccount(account)
                .transactionNumber(request.transactionNumber().trim())
                .transactionDate(request.transactionDate())
                .valueDate(request.valueDate())
                .amount(money(request.amount()))
                .direction(direction)
                .reference(request.reference())
                .description(request.description())
                .status("UNRECONCILED")
                .active(true)
                .build());
    }

    public BankTransaction reconcile(BankReconciliationRequest request) {
        BankAccount account = bankAccounts.findByAccountNumber(request.bankAccountNumber().trim())
                .filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active bank account not found: " + request.bankAccountNumber()));
        BankTransaction transaction = transactions.findByBankAccountIdAndTransactionNumber(account.getId(), request.transactionNumber().trim())
                .orElseThrow(() -> new InvalidWorkflowException("Bank transaction not found: " + request.transactionNumber()));
        if ("RECONCILED".equals(transaction.getStatus()))
            throw new InvalidWorkflowException("Bank transaction is already reconciled.");
        if (transaction.getAccountingDocumentId() != null)
            throw new InvalidWorkflowException("Bank transaction is already linked to an accounting document.");

        AccountingDocument document = documents.findByDocumentNumber(request.accountingDocumentNumber().trim())
                .filter(x -> "POSTED".equals(x.getStatus()))
                .orElseThrow(() -> new InvalidWorkflowException("Posted accounting document not found: " + request.accountingDocumentNumber()));

        if (transactions.findFirstByAccountingDocumentId(document.getId()).isPresent())
            throw new InvalidWorkflowException("Accounting document is already reconciled to another bank transaction.");

        if (!document.getCurrency().equalsIgnoreCase(account.getCurrency()))
            throw new InvalidWorkflowException("Bank transaction currency and accounting document currency do not match.");

        BigDecimal bankDebit = document.getLines().stream()
                .filter(l -> l.getGlAccount().getAccountCode().equals(account.getGlAccountCode()))
                .map(AccountingLine::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bankCredit = document.getLines().stream()
                .filter(l -> l.getGlAccount().getAccountCode().equals(account.getGlAccountCode()))
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
        financeQueryService.trialBalance().forEach(x -> balances.put(x.accountCode(), x.debitBalance().subtract(x.creditBalance())));
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
        if(inflow.compareTo(BigDecimal.ZERO)==0 && outflow.compareTo(BigDecimal.ZERO)==0)
            throw new InvalidWorkflowException("Liquidity forecast must contain an inflow or outflow.");
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

    private String currency(String x){
        String c=x==null||x.isBlank()?"USD":x.trim().toUpperCase(Locale.ROOT);
        if(!c.matches("[A-Z]{3}")) throw new InvalidWorkflowException("Currency must be a 3-letter ISO code.");
        return c;
    }

    private BigDecimal money(BigDecimal v){
        if(v==null||v.compareTo(BigDecimal.ZERO)<0) throw new InvalidWorkflowException("Amount must be zero or greater.");
        return v.setScale(2,RoundingMode.HALF_UP);
    }
}