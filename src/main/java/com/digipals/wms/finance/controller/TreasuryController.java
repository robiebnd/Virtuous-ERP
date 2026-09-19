package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.service.TreasuryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/finance/treasury")
@RequiredArgsConstructor
public class TreasuryController {
    private final TreasuryService service;

    @GetMapping("/risk-limits") public List<TreasuryRiskLimit> riskLimits(){return service.riskLimits();}
    @PostMapping("/risk-limits") public ResponseEntity<TreasuryRiskLimit> riskLimit(@Valid @RequestBody TreasuryRiskLimitRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveRiskLimit(request));}

    @GetMapping("/instruments") public List<TreasuryInstrument> instruments(){return service.instruments();}

    @PostMapping("/instruments") public ResponseEntity<TreasuryInstrument> createInstrument(@Valid @RequestBody TreasuryInstrumentRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.createInstrument(request));}

    @GetMapping("/cash-position")
    public Map<String,Object> cashPosition(){ return service.cashPosition(); }

    @GetMapping("/bank-transactions")
    public List<BankTransaction> transactions(@RequestParam(required=false) String bankAccountNumber){ return service.transactions(bankAccountNumber); }

    @PostMapping("/bank-transactions")
    public ResponseEntity<BankTransaction> capture(@Valid @RequestBody BankTransactionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.captureStatementLine(request));
    }

    @PostMapping("/bank-reconciliation")
    public BankTransaction reconcile(@Valid @RequestBody BankReconciliationRequest request){ return service.reconcile(request); }

    @GetMapping("/liquidity-forecasts")
    public List<LiquidityForecast> forecasts(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to){ return service.forecasts(from,to); }

    @PostMapping("/liquidity-forecasts")
    public ResponseEntity<LiquidityForecast> createForecast(@Valid @RequestBody LiquidityForecastRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createForecast(request));
    }
}