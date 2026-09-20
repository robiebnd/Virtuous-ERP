package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*; import java.time.*; import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class ConsolidationControlService {
 private final GroupReportingRunRepository runs; private final GroupReportingBalanceRepository balances; private final ConsolidationGroupRepository groups;
 private final ConsolidationJournalRepository journals; private final ConsolidationAuditEventRepository audits;
 private final CopaAllocationRuleRepository copaRules; private final CopaAllocationTargetRepository copaTargets; private final CopaAllocationRunRepository copaRuns;
 private final ProfitabilitySegmentRepository segments; private final ProfitabilityService profitability;
 private final CompanyCodeRepository companies; private final TaxAccountingService taxAccounting; private final TaxFilingRecordRepository filings;

 public ConsolidationJournal postJournal(ConsolidationJournalRequest r){
   GroupReportingRun run=runs.findById(r.runId()).orElseThrow(()->new InvalidWorkflowException("Consolidation run not found."));
   if(!"COMPLETED".equals(run.getStatus())) throw new InvalidWorkflowException("Consolidation journals can only be posted to a completed run.");
   if(r.lines().size()<2) throw new InvalidWorkflowException("Consolidation journal requires at least two lines.");
   BigDecimal debit=BigDecimal.ZERO,credit=BigDecimal.ZERO; int n=1;
   ConsolidationJournal j=ConsolidationJournal.builder().run(run).journalNumber("CJ-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT)).journalType(r.journalType().trim().toUpperCase(Locale.ROOT)).description(r.description().trim()).postingDate(r.postingDate()).status("POSTED").totalDebit(BigDecimal.ZERO).totalCredit(BigDecimal.ZERO).postedBy(r.postedBy().trim()).postedAt(LocalDateTime.now()).build();
   for(ConsolidationJournalRequest.Line l:r.lines()){
     BigDecimal d=nvl(l.debit()),c=nvl(l.credit());
     if(d.signum()>0&&c.signum()>0||d.signum()<0||c.signum()<0) throw new InvalidWorkflowException("Consolidation journal lines must contain non-negative debit or credit values.");
     if(d.signum()==0&&c.signum()==0) continue; debit=debit.add(d);credit=credit.add(c);
     j.getLines().add(ConsolidationJournalLine.builder().journal(j).lineNumber(n++).accountCode(l.accountCode().trim()).debit(d.setScale(2,RoundingMode.HALF_UP)).credit(c.setScale(2,RoundingMode.HALF_UP)).companyCode(l.companyCode()).partnerCompanyCode(l.partnerCompanyCode()).description(l.description()).build());
   }
   debit=debit.setScale(2,RoundingMode.HALF_UP);credit=credit.setScale(2,RoundingMode.HALF_UP);
   if(debit.signum()<=0||debit.compareTo(credit)!=0) throw new InvalidWorkflowException("Consolidation journal must balance.");
   j.setTotalDebit(debit);j.setTotalCredit(credit);j=journals.save(j);
   for(ConsolidationJournalLine line:j.getLines()){
     ConsolidationJournalLine l=line;
     GroupReportingBalance b=balances.findByRunIdOrderByAccountCodeAscCompanyCodeAsc(run.getId()).stream().filter(x->"GROUP".equalsIgnoreCase(x.getCompanyCode())&&x.getAccountCode().equals(l.getAccountCode())).findFirst().orElseGet(()->balances.save(GroupReportingBalance.builder().run(run).companyCode("GROUP").accountCode(l.getAccountCode()).accountName("Consolidation journal "+l.getAccountCode()).accountType("ADJUSTMENT").localDebit(BigDecimal.ZERO).localCredit(BigDecimal.ZERO).fxRate(BigDecimal.ONE).translatedDebit(BigDecimal.ZERO).translatedCredit(BigDecimal.ZERO).eliminationDebit(BigDecimal.ZERO).eliminationCredit(BigDecimal.ZERO).finalDebit(BigDecimal.ZERO).finalCredit(BigDecimal.ZERO).build()));
     b.setFinalDebit(b.getFinalDebit().add(l.getDebit())); b.setFinalCredit(b.getFinalCredit().add(l.getCredit())); balances.save(b);
   }
   audit(run.getGroup(),run,j,"JOURNAL_POSTED","POSTED",j.getJournalNumber(),j.getDescription(),r.postedBy());
   return j;
 }
 public List<ConsolidationJournal> journals(UUID runId){return journals.findByRunIdOrderByPostedAtDesc(runId);}
 public List<ConsolidationAuditEvent> audit(UUID groupId){return audits.findByGroupIdOrderByEventTimeDesc(groupId);}
 public List<ConsolidationAuditEvent> runAudit(UUID runId){return audits.findByRunIdOrderByEventTimeDesc(runId);}

 public CopaAllocationRule saveRule(CopaAllocationRuleRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT); companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   if(copaRules.findByCompanyCodeAndRuleCode(company,r.ruleCode().trim()).isPresent()) throw new InvalidWorkflowException("CO-PA allocation rule already exists.");
   ProfitabilitySegment source=segments.findById(r.sourceSegmentId()).orElseThrow(()->new InvalidWorkflowException("Source profitability segment not found."));
   if(!company.equalsIgnoreCase(source.getCompanyCode())) throw new InvalidWorkflowException("Source segment does not belong to the selected company.");
   return copaRules.save(CopaAllocationRule.builder().companyCode(company).ruleCode(r.ruleCode().trim().toUpperCase(Locale.ROOT)).ruleName(r.ruleName().trim()).sourceSegment(source).driverType(r.driverType().trim().toUpperCase(Locale.ROOT)).build());
 }
 public CopaAllocationTarget addTarget(CopaAllocationTargetRequest r){
   CopaAllocationRule rule=copaRules.findById(r.ruleId()).orElseThrow(()->new InvalidWorkflowException("CO-PA allocation rule not found."));
   ProfitabilitySegment target=segments.findById(r.targetSegmentId()).orElseThrow(()->new InvalidWorkflowException("Target profitability segment not found."));
   if(!rule.getCompanyCode().equalsIgnoreCase(target.getCompanyCode())) throw new InvalidWorkflowException("Target segment does not belong to the rule company.");
   return copaTargets.save(CopaAllocationTarget.builder().rule(rule).targetSegment(target).allocationPercent(r.allocationPercent()).build());
 }
 public List<CopaAllocationRule> rules(String company){return copaRules.findByCompanyCodeAndActiveTrueOrderByRuleCode(company.trim().toUpperCase(Locale.ROOT));}
 public CopaAllocationRun runCopa(UUID ruleId,CopaAllocationRunRequest r){
   CopaAllocationRule rule=copaRules.findById(ruleId).orElseThrow(()->new InvalidWorkflowException("CO-PA allocation rule not found."));
   if(copaRuns.findByRuleIdAndFiscalYearAndPeriodNumber(ruleId,r.fiscalYear(),r.periodNumber()).isPresent()) throw new InvalidWorkflowException("CO-PA allocation already run for this rule and period.");
   List<CopaAllocationTarget> targets=copaTargets.findByRuleIdAndActiveTrue(ruleId);
   BigDecimal totalPct=targets.stream().map(CopaAllocationTarget::getAllocationPercent).reduce(BigDecimal.ZERO,BigDecimal::add);
   if(totalPct.compareTo(BigDecimal.valueOf(100))!=0) throw new InvalidWorkflowException("CO-PA allocation targets must total exactly 100%. Current total: "+totalPct+"%.");
   ProfitabilityReportLine source=profitability.report(rule.getCompanyCode(),r.fiscalYear(),r.periodNumber()).stream().filter(x->x.segmentId().equals(rule.getSourceSegment().getId())).findFirst().orElse(null);
   BigDecimal amount=source==null?BigDecimal.ZERO:source.cost();
   if(amount.signum()<=0) throw new InvalidWorkflowException("No positive source cost is available for the selected CO-PA segment and period.");
   return copaRuns.save(CopaAllocationRun.builder().rule(rule).fiscalYear(r.fiscalYear()).periodNumber(r.periodNumber()).allocatedAmount(amount.setScale(2,RoundingMode.HALF_UP)).status("POSTED").runAt(LocalDateTime.now()).runBy(r.runBy().trim()).build());
 }
 public List<CopaAllocationRun> copaRuns(String company){return copaRuns.findByRuleCompanyCodeOrderByRunAtDesc(company.trim().toUpperCase(Locale.ROOT));}

 public TaxFilingRecord prepareFiling(TaxFilingRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT); companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   if(r.periodEnd().isBefore(r.periodStart())) throw new InvalidWorkflowException("Tax filing period is invalid.");
   TaxReportSummary s=taxAccounting.report(company,r.periodStart(),r.periodEnd());
   TaxFilingRecord f=filings.findByCompanyCodeAndTaxTypeAndPeriodStartAndPeriodEnd(company,r.taxType().trim().toUpperCase(Locale.ROOT),r.periodStart(),r.periodEnd()).orElseGet(TaxFilingRecord::new);
   if("FILED".equals(f.getStatus())) throw new InvalidWorkflowException("Tax filing is already filed and locked.");
   f.setCompanyCode(company);f.setTaxType(r.taxType().trim().toUpperCase(Locale.ROOT));f.setPeriodStart(r.periodStart());f.setPeriodEnd(r.periodEnd());f.setStatus("READY");f.setTaxableBase(s.lines().stream().map(TaxReportLine::taxableBase).reduce(BigDecimal.ZERO,BigDecimal::add));f.setOutputTax(s.outputTax());f.setInputTax(s.inputTax());f.setRecoverableInputTax(s.recoverableInputTax());f.setNetTax(s.netTax());f.setFilingReference(r.filingReference());f.setNotes(r.notes());return filings.save(f);
 }
 public TaxFilingRecord file(TaxFilingRequest r){
   TaxFilingRecord f=filings.findByCompanyCodeAndTaxTypeAndPeriodStartAndPeriodEnd(r.companyCode().trim().toUpperCase(Locale.ROOT),r.taxType().trim().toUpperCase(Locale.ROOT),r.periodStart(),r.periodEnd()).orElseThrow(()->new InvalidWorkflowException("Prepare the tax filing before filing."));
   if(!"READY".equals(f.getStatus())) throw new InvalidWorkflowException("Tax filing must be READY before filing.");
   f.setStatus("FILED");f.setFilingReference(r.filingReference());f.setFiledBy(r.filedBy()==null?"SYSTEM":r.filedBy().trim());f.setFiledAt(LocalDateTime.now());return filings.save(f);
 }
 public List<TaxFilingRecord> filings(String company){return filings.findByCompanyCodeOrderByPeriodEndDesc(company.trim().toUpperCase(Locale.ROOT));}

 private void audit(ConsolidationGroup group,GroupReportingRun run,ConsolidationJournal journal,String type,String status,String ref,String details,String actor){audits.save(ConsolidationAuditEvent.builder().group(group).run(run).journal(journal).eventType(type).eventStatus(status).eventTime(LocalDateTime.now()).actor(actor==null||actor.isBlank()?"SYSTEM":actor).referenceNumber(ref).details(details).build());}
 private BigDecimal nvl(BigDecimal v){return v==null?BigDecimal.ZERO:v;}
}