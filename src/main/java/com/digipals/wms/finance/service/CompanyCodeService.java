package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.CompanyCodeRequest;
import com.digipals.wms.finance.entity.CompanyCode;
import com.digipals.wms.finance.repository.CompanyCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.Year;\nimport com.digipals.wms.finance.dto.FiscalPeriodRequest;

@Service @RequiredArgsConstructor @Transactional
public class CompanyCodeService {
 private final CompanyCodeRepository repository;
 private final FiscalPeriodService fiscalPeriods;

 public List<CompanyCode> list(){ return repository.findByActiveTrueOrderByCompanyCode(); }
 public CompanyCode save(CompanyCodeRequest r){
   String code=normalize(r.companyCode());
   if(repository.findByCompanyCodeIgnoreCase(code).isPresent()) throw new InvalidWorkflowException("Company code already exists: "+code);
   String currency=currency(r.functionalCurrency());
   CompanyCode x=CompanyCode.builder().companyCode(code).companyName(r.companyName().trim()).countryCode(r.countryCode().trim().toUpperCase(Locale.ROOT)).functionalCurrency(currency).fiscalYearVariant(r.fiscalYearVariant()==null||r.fiscalYearVariant().isBlank()?"CALENDAR":r.fiscalYearVariant().trim().toUpperCase(Locale.ROOT)).reportingCurrency(r.reportingCurrency()==null?currency:currency(r.reportingCurrency())).build();
   CompanyCode saved=repository.save(x);\n   fiscalPeriods.createYear(new FiscalPeriodRequest(code,Year.now().getValue()));\n   return saved;
 }
 public CompanyCode get(String code){ return repository.findByCompanyCodeIgnoreCase(normalize(code)).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+code)); }
 public void ensurePostingReady(String code,int year){ get(code); if(fiscalPeriods.list(normalize(code),year).size()!=12) throw new InvalidWorkflowException("Fiscal year "+year+" is not configured for "+code+"."); }
 private String normalize(String s){if(s==null||s.isBlank())throw new InvalidWorkflowException("Company code is required.");return s.trim().toUpperCase(Locale.ROOT);}
 private String currency(String s){String x=s==null?"":s.trim().toUpperCase(Locale.ROOT);if(!x.matches("[A-Z]{3}"))throw new InvalidWorkflowException("Currency must be a 3-letter ISO code.");return x;}
}