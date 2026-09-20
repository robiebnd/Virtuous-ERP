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
public class ProfitabilityService {
 private final ProfitabilitySegmentRepository segments; private final AccountingLineRepository lines; private final CompanyCodeRepository companies; private final GlAccountRepository glAccounts; private final ProfitabilityPostingRepository profitabilityPostings; private final FinancePostingService postingService;
 public ProfitabilitySegment saveSegment(ProfitabilitySegmentRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT); companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   if(segments.findBySegmentCodeIgnoreCase(r.segmentCode().trim()).isPresent()) throw new InvalidWorkflowException("Profitability segment already exists.");
   return segments.save(ProfitabilitySegment.builder().segmentCode(r.segmentCode().trim().toUpperCase()).segmentName(r.segmentName().trim()).companyCode(company).customerId(r.customerId()).productCode(r.productCode()).salesChannel(r.salesChannel()).marketRegion(r.marketRegion()).customerGroup(r.customerGroup()).productGroup(r.productGroup()).active(true).build());
 }
 public List<ProfitabilitySegment> segments(String company){return segments.findByCompanyCodeOrderBySegmentCode(company.trim().toUpperCase(Locale.ROOT));}
 public AccountingDocument post(ProfitabilityPostRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT);
   ProfitabilitySegment segment=segments.findById(r.segmentId()).filter(x->x.getCompanyCode().equalsIgnoreCase(company)&&x.isActive()).orElseThrow(()->new InvalidWorkflowException("Active profitability segment not found for company: "+company));
   GlAccount account=glAccounts.findByAccountCode(r.accountCode().trim()).orElseThrow(()->new InvalidWorkflowException("GL account not configured: "+r.accountCode()));
   BigDecimal amount=r.amount().setScale(2,RoundingMode.HALF_UP);
   if(amount.signum()<=0) throw new InvalidWorkflowException("Profitability posting amount must be greater than zero.");
   boolean revenue=Boolean.TRUE.equals(r.revenue());
   if(revenue && !"REVENUE".equalsIgnoreCase(account.getAccountType())) throw new InvalidWorkflowException("Revenue profitability postings require a revenue GL account.");
   if(!revenue && !"EXPENSE".equalsIgnoreCase(account.getAccountType())) throw new InvalidWorkflowException("Cost profitability postings require an expense GL account.");
   UUID ref=UUID.nameUUIDFromBytes(("COPA_POST:"+company+":"+r.segmentId()+":"+r.accountCode()+":"+r.postingDate()+":"+amount+":"+revenue).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   List<FinancePostingService.PostingLine> postingLines;
   if(revenue){
     postingLines=List.of(
       new FinancePostingService.PostingLine("120000",amount,BigDecimal.ZERO,null,null,null,null,"CO-PA revenue",null,null,null,null,BigDecimal.ZERO,BigDecimal.ZERO,null),
       new FinancePostingService.PostingLine(account.getAccountCode(),BigDecimal.ZERO,amount,null,null,null,null,"CO-PA revenue",null,null,null,null,BigDecimal.ZERO,BigDecimal.ZERO,segment.getId()));
   } else {
     postingLines=List.of(
       new FinancePostingService.PostingLine(account.getAccountCode(),amount,BigDecimal.ZERO,null,null,null,null,"CO-PA cost",null,null,null,null,BigDecimal.ZERO,BigDecimal.ZERO,segment.getId()),
       new FinancePostingService.PostingLine("210000",BigDecimal.ZERO,amount,null,null,null,null,"CO-PA cost",null,null,null,null,BigDecimal.ZERO,BigDecimal.ZERO,null));
   }
   AccountingDocument document=postingService.postBalancedAtDate(company,"COPA_POSTING","COPA_POSTING",ref,r.segmentId().toString(),r.currency(),"CO-PA posting "+segment.getSegmentCode(),postingLines,r.postingDate().atStartOfDay());
   AccountingLine pAndLLine=document.getLines().stream().filter(x->x.getGlAccount().getAccountCode().equals(account.getAccountCode())).findFirst().orElseThrow();
   profitabilityPostings.save(ProfitabilityPosting.builder().accountingLine(pAndLLine).segment(segment).postingDate(r.postingDate()).revenueAmount(revenue?amount:BigDecimal.ZERO).costAmount(revenue?BigDecimal.ZERO:amount).quantity(r.quantity()==null?BigDecimal.ZERO:r.quantity()).currency(r.currency().trim().toUpperCase(Locale.ROOT)).build());
   return document;
 }
 public List<ProfitabilityReportLine> report(String company,int year,int period){
   String code=company.trim().toUpperCase(Locale.ROOT); LocalDate start=LocalDate.of(year,period,1), end=start.withDayOfMonth(start.lengthOfMonth()).plusDays(1);
   Map<UUID,BigDecimal[]> totals=new HashMap<>();
   for(Object[] row:lines.profitabilityBySegment(code,start.atStartOfDay(),end.atStartOfDay())){UUID id=(UUID)row[0];String type=(String)row[1];BigDecimal debit=(BigDecimal)row[2],credit=(BigDecimal)row[3];BigDecimal net=credit.subtract(debit);BigDecimal[] t=totals.computeIfAbsent(id,k->new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO});if("REVENUE".equalsIgnoreCase(type))t[0]=t[0].add(net);else if("EXPENSE".equalsIgnoreCase(type))t[1]=t[1].add(debit.subtract(credit));}
   return totals.entrySet().stream().map(e->{ProfitabilitySegment s=segments.findById(e.getKey()).orElseThrow();BigDecimal revenue=e.getValue()[0],cost=e.getValue()[1];return new ProfitabilityReportLine(s.getId(),s.getSegmentCode(),s.getSegmentName(),s.getProductCode(),s.getSalesChannel(),s.getMarketRegion(),s.getCustomerGroup(),s.getProductGroup(),revenue,cost,revenue.subtract(cost));}).sorted(Comparator.comparing(ProfitabilityReportLine::segmentCode)).toList();
 }

}