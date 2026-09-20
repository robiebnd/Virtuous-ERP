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
 private final FiscalPeriodService fiscalPeriods;
 private final ConsolidationAdjustmentRepository adjustments;
 private final GroupAccountMappingRepository accountMappings;
 private final ConsolidationNciResultRepository nciResults;

 public GroupReportingRun run(UUID groupId, GroupReportingRunRequest request){
   ConsolidationGroup group=groups.findById(groupId).orElseThrow(()->new InvalidWorkflowException("Consolidation group not found."));
   List<ConsolidationGroupUnit> memberships=groupUnits.findByIdGroupId(groupId);
   if(memberships.isEmpty()) throw new InvalidWorkflowException("No company codes are assigned to the consolidation group.");
   LocalDate start=LocalDate.of(request.fiscalYear(),request.periodNumber(),1);
   LocalDate end=start.withDayOfMonth(start.lengthOfMonth()).plusDays(1);
   List<String> companyCodes=new ArrayList<>();
   for(ConsolidationGroupUnit m:memberships){ConsolidationUnit u=units.findById(m.getId().getUnitId()).orElseThrow(()->new InvalidWorkflowException("Consolidation unit not found.")); if(!Boolean.TRUE.equals(u.getActive()))continue; companies.findByCompanyCodeIgnoreCase(u.getCompanyCode()).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+u.getCompanyCode())); FiscalPeriod fp=fiscalPeriods.find(u.getCompanyCode(),request.fiscalYear(),request.periodNumber()); if("OPEN".equals(fp.getStatus())) throw new InvalidWorkflowException("Local fiscal period must be closed before group reporting: "+u.getCompanyCode()+"/"+request.fiscalYear()+"/"+request.periodNumber()); companyCodes.add(u.getCompanyCode());}
   if(companyCodes.isEmpty()) throw new InvalidWorkflowException("No active company codes are assigned to the consolidation group.");
   if(group.getReportingCurrency()==null||group.getReportingCurrency().isBlank()) throw new InvalidWorkflowException("Consolidation group reporting currency is required.");
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
   List<GroupAccountMapping> mappings=accountMappings.findByGroupIdAndActiveTrueOrderByCompanyCodeAscLocalAccountCodeAsc(groupId);
   Map<String,GroupAccountMapping> mappingByKey=new HashMap<>();
   for(GroupAccountMapping m:mappings) mappingByKey.put(m.getCompanyCode().toUpperCase(Locale.ROOT)+"|"+m.getLocalAccountCode(),m);
   for(GroupReportingBalance b:balances.findByRunIdOrderByAccountCodeAscCompanyCodeAsc(run.getId())){
     GroupAccountMapping mapping=mappingByKey.get(b.getCompanyCode().toUpperCase(Locale.ROOT)+"|"+b.getAccountCode());
     if(mapping!=null){ b.setAccountCode(mapping.getGroupAccountCode()); b.setAccountName(mapping.getGroupAccountName()); b.setAccountType(mapping.getGroupAccountType()); }
b.setFinalDebit(b.getTranslatedDebit().subtract(b.getEliminationDebit()).max(BigDecimal.ZERO));b.setFinalCredit(b.getTranslatedCredit().subtract(b.getEliminationCredit()).max(BigDecimal.ZERO));balances.save(b);totalD=totalD.add(b.getFinalDebit());totalC=totalC.add(b.getFinalCredit());}
   for(ConsolidationAdjustment a:adjustments.findByGroupIdAndPeriodStartAndStatus(groupId,start,"POSTED")){
     BigDecimal debit=a.getDebit()==null?BigDecimal.ZERO:a.getDebit(), credit=a.getCredit()==null?BigDecimal.ZERO:a.getCredit();
     String key="GROUP|"+a.getAccountCode(); GroupReportingBalance b=byKey.get(key);
     if(b==null){b=GroupReportingBalance.builder().run(run).companyCode("GROUP").accountCode(a.getAccountCode()).accountName("Consolidation adjustment").accountType("ADJUSTMENT").localDebit(BigDecimal.ZERO).localCredit(BigDecimal.ZERO).fxRate(BigDecimal.ONE).translatedDebit(BigDecimal.ZERO).translatedCredit(BigDecimal.ZERO).eliminationDebit(BigDecimal.ZERO).eliminationCredit(BigDecimal.ZERO).finalDebit(BigDecimal.ZERO).finalCredit(BigDecimal.ZERO).build(); byKey.put(key,b); balances.save(b);}
     b.setFinalDebit(b.getFinalDebit().add(debit)); b.setFinalCredit(b.getFinalCredit().add(credit)); balances.save(b); totalD=totalD.add(debit); totalC=totalC.add(credit);
   }
   BigDecimal totalNci=BigDecimal.ZERO;
   for(ConsolidationGroupUnit membership:memberships){
     ConsolidationUnit unit=units.findById(membership.getId().getUnitId()).orElseThrow(()->new InvalidWorkflowException("Consolidation unit not found."));
     BigDecimal ownership=unit.getOwnershipPercent()==null?BigDecimal.valueOf(100):unit.getOwnershipPercent();
     if(ownership.compareTo(BigDecimal.ZERO)<0||ownership.compareTo(BigDecimal.valueOf(100))>0) throw new InvalidWorkflowException("Ownership percentage must be between 0 and 100 for "+unit.getUnitCode()+".");
     BigDecimal nciPercent=BigDecimal.valueOf(100).subtract(ownership);
     if(nciPercent.signum()==0) continue;
     BigDecimal assets=BigDecimal.ZERO, liabilities=BigDecimal.ZERO, equity=BigDecimal.ZERO;
     for(GroupReportingBalance b:balances.findByRunIdOrderByAccountCodeAscCompanyCodeAsc(run.getId())){
       if(!unit.getCompanyCode().equalsIgnoreCase(b.getCompanyCode())) continue;
       BigDecimal signed=b.getFinalDebit().subtract(b.getFinalCredit());
       if("ASSET".equalsIgnoreCase(b.getAccountType())) assets=assets.add(signed);
       else if("LIABILITY".equalsIgnoreCase(b.getAccountType())) liabilities=liabilities.add(b.getFinalCredit().subtract(b.getFinalDebit()));
       else if("EQUITY".equalsIgnoreCase(b.getAccountType())) equity=equity.add(b.getFinalCredit().subtract(b.getFinalDebit()));
     }
     BigDecimal netAssets=assets.subtract(liabilities).setScale(2,RoundingMode.HALF_UP);
     BigDecimal nci=netAssets.multiply(nciPercent).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
     nciResults.save(ConsolidationNciResult.builder().run(run).unit(unit).ownershipPercent(ownership).nciPercent(nciPercent).netAssets(netAssets).nciAmount(nci).build());
     totalNci=totalNci.add(nci);
   }
   run.setNciAmount(totalNci.setScale(2,RoundingMode.HALF_UP));
   BigDecimal imbalance=totalD.subtract(totalC).setScale(2,RoundingMode.HALF_UP);
   if(imbalance.compareTo(BigDecimal.ZERO)!=0){GroupReportingBalance fx=GroupReportingBalance.builder().run(run).companyCode("GROUP").accountCode("3310").accountName("Foreign Currency Translation Reserve").accountType("EQUITY").localDebit(BigDecimal.ZERO).localCredit(BigDecimal.ZERO).fxRate(BigDecimal.ONE).translatedDebit(imbalance.signum()<0?imbalance.abs():BigDecimal.ZERO).translatedCredit(imbalance.signum()>0?imbalance:BigDecimal.ZERO).eliminationDebit(BigDecimal.ZERO).eliminationCredit(BigDecimal.ZERO).finalDebit(imbalance.signum()<0?imbalance.abs():BigDecimal.ZERO).finalCredit(imbalance.signum()>0?imbalance:BigDecimal.ZERO).build();balances.save(fx);totalD=totalD.add(fx.getFinalDebit());totalC=totalC.add(fx.getFinalCredit());translationAdjustment=imbalance.negate();}
   run.setTotalDebit(totalD.setScale(2,RoundingMode.HALF_UP));run.setTotalCredit(totalC.setScale(2,RoundingMode.HALF_UP));run.setTranslationAdjustment(translationAdjustment.setScale(2,RoundingMode.HALF_UP));run.setStatus("COMPLETED");run.setCompletedAt(LocalDateTime.now());return runs.save(run);
 }
 public List<GroupAccountMapping> mappings(UUID groupId){return accountMappings.findByGroupIdAndActiveTrueOrderByCompanyCodeAscLocalAccountCodeAsc(groupId);}
 public GroupAccountMapping saveMapping(com.digipals.wms.finance.dto.GroupAccountMappingRequest r){
   ConsolidationGroup group=groups.findById(r.groupId()).orElseThrow(()->new InvalidWorkflowException("Consolidation group not found."));
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT), local=r.localAccountCode().trim(), target=r.groupAccountCode().trim();
   companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   accountMappings.findByGroupIdAndCompanyCodeAndLocalAccountCode(group.getId(),company,local).ifPresent(x->{throw new InvalidWorkflowException("Group account mapping already exists for "+company+"/"+local+".");});
   return accountMappings.save(GroupAccountMapping.builder().group(group).companyCode(company).localAccountCode(local).groupAccountCode(target).groupAccountName(r.groupAccountName().trim()).groupAccountType(r.groupAccountType().trim().toUpperCase(Locale.ROOT)).active(true).build());
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