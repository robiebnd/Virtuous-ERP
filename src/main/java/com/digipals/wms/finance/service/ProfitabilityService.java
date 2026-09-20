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
 private final ProfitabilitySegmentRepository segments; private final AccountingLineRepository lines; private final CompanyCodeRepository companies;
 public ProfitabilitySegment saveSegment(ProfitabilitySegmentRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT); companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   if(segments.findBySegmentCodeIgnoreCase(r.segmentCode().trim()).isPresent()) throw new InvalidWorkflowException("Profitability segment already exists.");
   return segments.save(ProfitabilitySegment.builder().segmentCode(r.segmentCode().trim().toUpperCase()).segmentName(r.segmentName().trim()).companyCode(company).customerId(r.customerId()).productCode(r.productCode()).salesChannel(r.salesChannel()).marketRegion(r.marketRegion()).customerGroup(r.customerGroup()).productGroup(r.productGroup()).active(true).build());
 }
 public List<ProfitabilitySegment> segments(String company){return segments.findByCompanyCodeOrderBySegmentCode(company.trim().toUpperCase(Locale.ROOT));}
 public List<ProfitabilityReportLine> report(String company,int year,int period){
   String code=company.trim().toUpperCase(Locale.ROOT); LocalDate start=LocalDate.of(year,period,1), end=start.withDayOfMonth(start.lengthOfMonth()).plusDays(1);
   Map<UUID,BigDecimal[]> totals=new HashMap<>();
   for(Object[] row:lines.profitabilityBySegment(code,start.atStartOfDay(),end.atStartOfDay())){UUID id=(UUID)row[0];String type=(String)row[1];BigDecimal debit=(BigDecimal)row[2],credit=(BigDecimal)row[3];BigDecimal net=credit.subtract(debit);BigDecimal[] t=totals.computeIfAbsent(id,k->new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO});if("REVENUE".equalsIgnoreCase(type))t[0]=t[0].add(net);else if("EXPENSE".equalsIgnoreCase(type))t[1]=t[1].add(debit.subtract(credit));}
   return totals.entrySet().stream().map(e->{ProfitabilitySegment s=segments.findById(e.getKey()).orElseThrow();BigDecimal revenue=e.getValue()[0],cost=e.getValue()[1];return new ProfitabilityReportLine(s.getId(),s.getSegmentCode(),s.getSegmentName(),s.getProductCode(),s.getSalesChannel(),s.getMarketRegion(),s.getCustomerGroup(),s.getProductGroup(),revenue,cost,revenue.subtract(cost));}).sorted(Comparator.comparing(ProfitabilityReportLine::segmentCode)).toList();
 }

}