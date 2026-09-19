package com.digipals.wms.finance.controller;
import com.digipals.wms.finance.dto.*; import com.digipals.wms.finance.entity.*; import com.digipals.wms.finance.repository.*; import com.digipals.wms.finance.service.AdvancedFinanceService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;

@RestController @RequestMapping("/api/finance/advanced") @RequiredArgsConstructor
public class AdvancedFinanceController {
 private final AdvancedFinanceService service; private final CreditProfileRepository credits; private final CollectionCaseRepository collections; private final FinanceDisputeRepository disputes; private final RevenueContractRepository contracts; private final TaxCodeRepository taxCodes; private final FxRateRepository fxRates; private final FundRepository funds; private final ProductCostEstimateRepository costs; private final AccrualTemplateRepository accruals; private final ConsolidationUnitRepository units; private final ConsolidationGroupRepository groups; private final ConsolidationGroupUnitRepository groupUnits; private final ConsolidationAdjustmentRepository adjustments; private final FundCommitmentRepository commitments; private final MaterialLedgerPeriodRepository materialLedger;

 @GetMapping("/credit-profiles") public List<CreditProfile> credits(){return credits.findAll();}
 @PostMapping("/credit-profiles") public ResponseEntity<CreditProfile> credit(@Valid @RequestBody CreditProfileRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveCredit(r));}
 @GetMapping("/collections") public List<CollectionCase> collections(@RequestParam(required=false)String status){return service.collectionWorklist(status);}
 @PostMapping("/collections") public ResponseEntity<CollectionCase> collection(@Valid @RequestBody CollectionCaseRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createCollection(r));}
 @GetMapping("/disputes") public List<FinanceDispute> disputes(@RequestParam(required=false)String status){return service.disputes(status);}
 @PostMapping("/disputes") public ResponseEntity<FinanceDispute> dispute(@Valid @RequestBody DisputeRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createDispute(r));}
 @GetMapping("/revenue-contracts") public List<RevenueContract> revenueContracts(){return contracts.findAll();}
 @PostMapping("/revenue-contracts") public ResponseEntity<RevenueContract> revenueContract(@Valid @RequestBody RevenueContractRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createContract(r));}
 @PostMapping("/revenue-obligations") public ResponseEntity<RevenueObligation> obligation(@Valid @RequestBody RevenueObligationRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.addObligation(r));}
 @GetMapping("/revenue-obligations/{contractId}") public List<RevenueObligation> obligations(@PathVariable UUID contractId){return service.obligations(contractId);}
 @PostMapping("/revenue-allocation") public RevenueObligation allocate(@Valid @RequestBody RevenueAllocationRequest r){return service.allocateRevenue(r);}
 @PostMapping("/revenue-recognition") public ResponseEntity<RevenueRecognitionEvent> recognize(@Valid @RequestBody RecognitionRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.recognize(r));}
 @GetMapping("/tax-codes") public List<TaxCode> taxCodes(){return taxCodes.findAll();}
 @PostMapping("/tax-codes") public ResponseEntity<TaxCode> taxCode(@Valid @RequestBody TaxCodeRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTaxCode(r));}
 @GetMapping("/tax-codes/{code}/calculate") public BigDecimal calculateTax(@PathVariable String code,@RequestParam BigDecimal taxable){return service.calculateTax(code,taxable);}
 @GetMapping("/fx-rates") public List<FxRate> fxRates(@RequestParam(required=false)LocalDate date){return service.fxRates(date);}
 @PostMapping("/fx-rates") public ResponseEntity<FxRate> fxRate(@Valid @RequestBody FxRateRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveFxRate(r));}
 @GetMapping("/funds") public List<Fund> funds(){return funds.findAll();}
 @PostMapping("/funds") public ResponseEntity<Fund> fund(@Valid @RequestBody FundRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveFund(r));}
 @PostMapping("/fund-commitments") public ResponseEntity<FundCommitment> commitment(@Valid @RequestBody FundCommitmentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.commitFund(r));}
 @GetMapping("/funds/{fundId}/commitments") public List<FundCommitment> commitments(@PathVariable UUID fundId){return commitments.findByFundIdOrderByCreatedAtDesc(fundId);}
 @GetMapping("/consolidation/units") public List<ConsolidationUnit> units(){return units.findAll();}
 @PostMapping("/consolidation/units") public ResponseEntity<ConsolidationUnit> unit(@Valid @RequestBody ConsolidationUnitRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveUnit(r));}
 @GetMapping("/consolidation/groups") public List<ConsolidationGroup> groups(){return groups.findAll();}
 @PostMapping("/consolidation/groups") public ResponseEntity<ConsolidationGroup> group(@Valid @RequestBody ConsolidationGroupRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveGroup(r));}
 @PostMapping("/consolidation/groups/{groupId}/units/{unitId}") public ConsolidationGroupUnit addUnit(@PathVariable UUID groupId,@PathVariable UUID unitId){return service.addUnitToGroup(groupId,unitId);}
 @GetMapping("/consolidation/groups/{groupId}/adjustments") public List<ConsolidationAdjustment> adjustments(@PathVariable UUID groupId){return adjustments.findByGroupIdOrderByPeriodStartDesc(groupId);}
 @PostMapping("/consolidation/adjustments") public ResponseEntity<ConsolidationAdjustment> adjustment(@Valid @RequestBody ConsolidationAdjustmentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.saveAdjustment(r));}
 @PostMapping("/product-costing") public ResponseEntity<ProductCostEstimate> cost(@Valid @RequestBody CostEstimateRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createCost(r));}
 @PostMapping("/product-costing/{id}/release") public ProductCostEstimate release(@PathVariable UUID id){return service.releaseCost(id);}
 @GetMapping("/product-costing/{productCode}") public List<ProductCostEstimate> productCosts(@PathVariable String productCode){return costs.findByProductCodeOrderByCostingDateDesc(productCode);}
 @PostMapping("/accrual-templates") public ResponseEntity<AccrualTemplate> accrual(@Valid @RequestBody AccrualTemplateRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createAccrual(r));}
 @GetMapping("/accrual-templates") public List<AccrualTemplate> accruals(){return accruals.findAll();}
 @PostMapping("/accrual-templates/{id}/run") public AccrualRun runAccrual(@PathVariable UUID id){return service.runAccrual(id);}
 @PostMapping("/material-ledger") public MaterialLedgerPeriod materialLedger(@Valid @RequestBody MaterialLedgerRequest r){return service.saveMaterialLedger(r);}
 @GetMapping("/material-ledger/{productCode}") public List<MaterialLedgerPeriod> materialLedger(@PathVariable String productCode){return materialLedger.findAll().stream().filter(x->x.getProductCode().equals(productCode)).toList();}
}