package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import com.digipals.wms.finance.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/finance/group-reporting")
@RequiredArgsConstructor
public class GroupReportingController {
 private final CompanyCodeRepository companies; private final CompanyCodeService companyService; private final GroupReportingService groupReporting; private final IntercompanyService intercompany; private final IntercompanyTransactionRepository intercompanyRepository; private final TaxAccountingService taxAccounting; private final TaxPostingRepository taxPostings; private final ProfitabilityService profitability;
 private final ConsolidationGroupRepository groups; private final ConsolidationUnitRepository units;

 @GetMapping("/company-codes") public List<CompanyCode> companyCodes(){return companyService.list();}
 @PostMapping("/company-codes") public ResponseEntity<CompanyCode> createCompanyCode(@Valid @RequestBody CompanyCodeRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(companyService.save(r));}
 @GetMapping("/company-codes/{code}") public CompanyCode companyCode(@PathVariable String code){return companyService.get(code);}

 @PostMapping("/intercompany") public ResponseEntity<IntercompanyTransaction> postIntercompany(@Valid @RequestBody IntercompanyRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(intercompany.post(r));}
 @GetMapping("/intercompany") public List<IntercompanyTransaction> intercompany(){return intercompany.list();}

 @PostMapping("/groups/{groupId}/runs") public ResponseEntity<GroupReportingRun> run(@PathVariable UUID groupId,@Valid @RequestBody GroupReportingRunRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(groupReporting.run(groupId,r));}
 @GetMapping("/groups/{groupId}/runs") public List<GroupReportingRun> runs(@PathVariable UUID groupId){return groupReporting.runs(groupId);}
 @GetMapping("/runs/{runId}/balances") public List<GroupReportingBalance> balances(@PathVariable UUID runId){return groupReporting.balances(runId);}

 @GetMapping("/tax-postings") public List<TaxPosting> taxPostingList(@RequestParam String companyCode){return taxPostings.findByCompanyCodeOrderByCreatedAtDesc(companyCode);}
 @PostMapping("/tax-postings") public ResponseEntity<TaxPosting> taxPost(@Valid @RequestBody TaxPostingRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(taxAccounting.post(r));}

 @GetMapping("/profitability-segments") public List<ProfitabilitySegment> segments(@RequestParam String companyCode){return profitability.segments(companyCode);}
 @PostMapping("/profitability-segments") public ResponseEntity<ProfitabilitySegment> segment(@Valid @RequestBody ProfitabilitySegmentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(profitability.saveSegment(r));}
 @GetMapping("/profitability-report") public List<ProfitabilityReportLine> profitabilityReport(@RequestParam String companyCode,@RequestParam int fiscalYear,@RequestParam int period){return profitability.report(companyCode,fiscalYear,period);}
}