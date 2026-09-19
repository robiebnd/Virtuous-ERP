package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.GroupReportingRunRequest;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class GroupReportingService {
 private final ConsolidationGroupRepository groups;
 private final ConsolidationGroupUnitRepository groupUnits;
 private final ConsolidationUnitRepository units;
 private final CompanyCodeRepository companies;
 private final AccountingLineRepository lines;
 private final GroupReportingRunRepository runs;
 private final GroupReportingBalanceRepository balances;
 private final IntercompanyTransactionRepository intercompany;
 private final FxRateRepository fxRates;

 public GroupReportingRun run(UUID groupId, GroupReportingRunRequest request){
   ConsolidationGroup group=groups.findById(groupId).orElseThrow(()->new InvalidWorkflowException("Consolidation group not found."));
   List<ConsolidationGroupUnit> memberships=groupUnits.findByIdGroupId(groupId);
   if(memberships.isEmpty()) throw new InvalidWorkflowException("No company codes are assigned to the consolidation group.");
   LocalDate start=LocalDate.of(request.fiscalYear(),request.periodNumber(),1);
   LocalDate end=start.withDayOfMonth(start.lengthOfMonth()).plusDays(1);
   List<String> companyCodes=new ArrayList<>();
   for(ConsolidationGroupUnit m:memberships){ConsolidationUnit u=units.findById(m.getId().getUnitId()).orElseThrow(()->new InvalidWorkflowException("Consolidation unit not found.")); if(!u.isActive())continue; companies.findByCompanyCodeIgnoreCase(u.getCompanyCode()).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+u.getCompanyCode())); companyCodes.add(u.getCompanyCode());}
   if(companyCodes.isEmpty()) throw new InvalidWorkflowException("No active company codes are assigned to the consolidation group.");
   if(runs.findByGroupIdAndFiscalYearAndPeriodNumber(groupId,request.fiscalYear(),request.periodNumber()).isPresent()) throw new InvalidWorkflowException("Group reporting run already exists for this group and period.");
   GroupReportingRun run=GroupReportingRun.builder().group(group).fiscalYear(request.fiscalYear()).periodNumber(request.periodNumber()).reportingCurrency(group.getReportingCurrency()).status("RUNNING").totalDebit(BigDecimal.ZERO).totalCredit(BigDecimal.ZERO).translationAdjustment(BigDecimal.ZERO).build();
   run=runs.save(run);
   Map<String,GroupReportingBalance> byKey=new LinkedHashMap<>();
   BigDecimal translationAdjustment=BigDecimal.ZERO;
   List<Object[]> raw=lines.groupPeriodBalances(companyCodes,start.atStartOfDay(),end.atStartOfDay());
   for(Object[] row:raw){
     String company=(String)row[0], account=(String)row[1], name=(String)row[2], type=(String)row[3];
     BigDecimal debit=(BigDecimal)row[4], credit=(BigDecimal)row[5];
     BigDecimal rate=rateFor(company,run.getReportingCurrency(),end.minusDays(1),type);
     BigDecimal td=debit.multiply(rate).setScale(2,RoundingMode.HALF_UP), tc=credit.multiply(rate).setScale(2,RoundingMode.HALF_UP);
     GroupReportingBalance b=GroupReportingBalance.builder().run(run).companyCode(company).accountCode(account).accountName(name).accountType(type).localDebit(debit).localCredit(credit).fxRate(rate).translatedDebit(td).translatedCredit(tc).eliminationDebit(BigDecimal.ZERO).eliminationCredit(BigDecimal.ZERO).finalDebit(td).finalCredit(tc).build();
     balances.save(b); byKey.put(company+"|"+account,b);
   }
   for(IntercompanyTransaction tx:intercompany.findByStatusOrderByTransactionDateDesc("POSTED")){
     if(tx.getTransactionDate().isBefore(start)||!tx.getTransactionDate().isBefore(end)||!companyCodes.contains(tx.getSourceCompanyCode())||!companyCodes.contains(tx.getTargetCompanyCode())) continue;
     BigDecimal rate=rateFor(tx.getCurrency(),run.getReportingCurrency(),tx.getTransactionDate(),"BALANCE");
     BigDecimal amount=tx.getAmount().multiply(rate).setScale(2,RoundingMode.HALF_UP);
     eliminate(byKey,tx.getSourceCompanyCode(),tx.getSourceDebitAccountCode(),amount,true);
     eliminate(byKey,tx.getSourceCompanyCode(),tx.getSourceCreditAccountCode(),amount,false);
     eliminate(byKey,tx.getTargetCompanyCode(),tx.getTargetDebitAccountCode(),amount,true);
     eliminate(byKey,tx.getTargetCompanyCode(),tx.getTargetCreditAccountCode(),amount,false);
   }
   BigDecimal totalD=BigDecimal.ZERO,totalC=BigDecimal.ZERO;
   for(GroupReportingBalance b:balances.findByRunIdOrderByAccountCodeAscCompanyCodeAsc(run.getId())){b.setFinalDebit(b.getTranslatedDebit().subtract(b.getEliminationDebit()).max(BigDecimal.ZERO));b.setFinalCredit(b.getTranslatedCredit().subtract(b.getEliminationCredit()).max(BigDecimal.ZERO));balances.save(b);totalD=totalD.add(b.getFinalDebit());totalC=totalC.add(b.getFinalCredit());}
   run.setTotalDebit(totalD.setScale(2,RoundingMode.HALF_UP));run.setTotalCredit(totalC.setScale(2,RoundingMode.HALF_UP));run.setTranslationAdjustment(translationAdjustment.setScale(2,RoundingMode.HALF_UP));run.setStatus("COMPLETED");run.setCompletedAt(LocalDateTime.now());return runs.save(run);
 }
 public List<GroupReportingRun> runs(UUID groupId){return runs.findByGroupIdOrderByFiscalYearDescPeriodNumberDesc(groupId);}
 public List<GroupReportingBalance> balances(UUID runId){return balances.findByRunIdOrderByAccountCodeAscCompanyCodeAsc(runId);}
 private void eliminate(Map<String,GroupReportingBalance> map,String company,String account,BigDecimal amount,boolean debit){if(account==null||account.isBlank())return;GroupReportingBalance b=map.get(company+"|"+account);if(b==null)return;if(debit)b.setEliminationDebit(b.getEliminationDebit().add(amount));else b.setEliminationCredit(b.getEliminationCredit().add(amount));}
 private BigDecimal rateFor(String from,String to,LocalDate date,String accountType){
   if(from.equalsIgnoreCase(to))return BigDecimal.ONE;
   String type=("REVENUE".equalsIgnoreCase(accountType)||"EXPENSE".equalsIgnoreCase(accountType))?"AVERAGE":"CLOSING";
   return fxRates.findTopByRateDateLessThanEqualAndFromCurrencyAndToCurrencyAndRateTypeOrderByRateDateDesc(date,from.toUpperCase(),to.toUpperCase(),type)
     .map(FxRate::getRate)
     .orElseGet(()->fxRates.findTopByRateDateLessThanEqualAndFromCurrencyAndToCurrencyAndRateTypeOrderByRateDateDesc(date,to.toUpperCase(),from.toUpperCase(),type)
       .map(x->BigDecimal.ONE.divide(x.getRate(),8,RoundingMode.HALF_UP))
       .orElseThrow(()->new InvalidWorkflowException("No "+type+" FX rate available from "+from+" to "+to+" for "+date+".")));
 }
}