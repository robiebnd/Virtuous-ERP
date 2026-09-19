package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceControlController {
 private final CostCenterRepository costCenters;
 private final BankAccountRepository bankAccounts;
 private final FixedAssetRepository fixedAssets;
 private final com.digipals.wms.finance.service.FixedAssetAccountingService fixedAssetAccountingService;

 @GetMapping("/cost-centers") public List<CostCenter> costCenters(){return costCenters.findByActiveTrueOrderByCodeAsc();}
 @PostMapping("/cost-centers") public ResponseEntity<CostCenter> createCostCenter(@RequestBody CostCenter x){return ResponseEntity.status(HttpStatus.CREATED).body(costCenters.save(x));}
 @GetMapping("/bank-accounts") public List<BankAccount> bankAccounts(){return bankAccounts.findByActiveTrueOrderByBankNameAsc();}
 @PostMapping("/bank-accounts") public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount x){x.setCurrency(x.getCurrency()==null?"USD":x.getCurrency().toUpperCase(Locale.ROOT));return ResponseEntity.status(HttpStatus.CREATED).body(bankAccounts.save(x));}
 @GetMapping("/fixed-assets") public List<FixedAsset> fixedAssets(){return fixedAssets.findAll();}
 @PostMapping("/fixed-assets") public ResponseEntity<FixedAsset> createFixedAsset(@RequestBody FixedAsset x){if(x.getAccumulatedDepreciation()==null)x.setAccumulatedDepreciation(BigDecimal.ZERO);if(x.getResidualValue()==null)x.setResidualValue(BigDecimal.ZERO);if(x.getStatus()==null)x.setStatus("ACTIVE");return ResponseEntity.status(HttpStatus.CREATED).body(fixedAssets.save(x));}
 @PostMapping("/fixed-assets/{id}/depreciate") public FixedAsset depreciate(@PathVariable UUID id,@RequestParam(required=false) Integer months){ return fixedAssetAccountingService.depreciate(id, months); }

}