package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.TaxPostingRequest;
import com.digipals.wms.finance.dto.TaxReportLine;
import com.digipals.wms.finance.dto.TaxReportSummary;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class TaxAccountingService {
 private final TaxCodeRepository taxCodes; private final TaxPostingRepository taxPostings; private final CompanyCodeRepository companies; private final FinancePostingService posting;
 public TaxPosting post(TaxPostingRequest r){
   String company=r.companyCode().trim().toUpperCase(Locale.ROOT), direction=r.inputOutput().trim().toUpperCase(Locale.ROOT);
   companies.findByCompanyCodeIgnoreCase(company).orElseThrow(()->new InvalidWorkflowException("Company code not found: "+company));
   if(!direction.equals("INPUT")&&!direction.equals("OUTPUT")) throw new InvalidWorkflowException("Tax direction must be INPUT or OUTPUT.");
   TaxCode tax=taxCodes.findByCompanyCodeAndTaxCodeIgnoreCase(company,r.taxCode().trim()).orElseThrow(()->new InvalidWorkflowException("Tax code not configured for "+company+": "+r.taxCode()));
   LocalDate postingDate=r.postingDate();
   if(tax.getEffectiveFrom()!=null && postingDate.isBefore(tax.getEffectiveFrom())) throw new InvalidWorkflowException("Tax code "+tax.getTaxCode()+" is not effective on "+postingDate+".");
   if(tax.getEffectiveTo()!=null && postingDate.isAfter(tax.getEffectiveTo())) throw new InvalidWorkflowException("Tax code "+tax.getTaxCode()+" expired on "+tax.getEffectiveTo()+".");
   BigDecimal base=r.taxableAmount().setScale(2,RoundingMode.HALF_UP), amount=base.multiply(tax.getRate()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
   BigDecimal recoverablePercent=tax.getRecoverablePercent()==null?BigDecimal.valueOf(100):tax.getRecoverablePercent();
   BigDecimal recoverable=direction.equals("INPUT")?amount.multiply(recoverablePercent).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP):BigDecimal.ZERO;
   BigDecimal nonRecoverable=direction.equals("INPUT")?amount.subtract(recoverable):BigDecimal.ZERO;
   String taxAccount=tax.getTaxAccountCode()!=null&&!tax.getTaxAccountCode().isBlank()?tax.getTaxAccountCode():(direction.equals("OUTPUT")?tax.getOutputAccountCode():tax.getInputAccountCode());
   if(taxAccount==null||taxAccount.isBlank()) throw new InvalidWorkflowException("Tax account is not configured for tax code "+tax.getTaxCode()+".");
   BigDecimal gross=base.add(amount);
   List<FinancePostingService.PostingLine> lines;
   if(direction.equals("OUTPUT")) lines=List.of(new FinancePostingService.PostingLine(r.baseDebitAccount(),gross,BigDecimal.ZERO,null,null,null,null,"Customer tax-inclusive receivable"),new FinancePostingService.PostingLine(r.baseCreditAccount(),BigDecimal.ZERO,base,null,null,null,null,"Revenue excluding output tax"),new FinancePostingService.PostingLine(taxAccount,BigDecimal.ZERO,amount,null,null,null,null,"Output VAT / sales tax"));
   else lines=List.of(new FinancePostingService.PostingLine(r.baseDebitAccount(),base.add(nonRecoverable),BigDecimal.ZERO,null,null,null,null,"Expense / inventory including non-recoverable input tax"),new FinancePostingService.PostingLine(taxAccount,recoverable,BigDecimal.ZERO,null,null,null,null,"Recoverable input VAT / sales tax"),new FinancePostingService.PostingLine(r.baseCreditAccount(),BigDecimal.ZERO,gross,null,null,null,null,"Supplier payable tax-inclusive"));
   UUID ref=UUID.nameUUIDFromBytes(("TAX:"+company+":"+r.referenceNumber()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   AccountingDocument doc=posting.postBalancedAtDate(company,r.documentType(),"TAX_DOCUMENT",ref,r.referenceNumber(),r.currency(),r.description(),lines,r.postingDate().atStartOfDay());
   return taxPostings.save(TaxPosting.builder().accountingDocument(doc).companyCode(company).taxCode(tax.getTaxCode()).taxType(tax.getTaxType()).jurisdiction(tax.getJurisdiction()).taxableBase(base).taxAmount(amount).inputOutput(direction).taxAccountCode(taxAccount).recoverableAmount(recoverable).build());
 }
 public List<TaxPosting> list(String company){return taxPostings.findByCompanyCodeOrderByCreatedAtDesc(company.trim().toUpperCase(Locale.ROOT));}
 public TaxReportSummary report(String company,LocalDate start,LocalDate end){
   String code=company.trim().toUpperCase(Locale.ROOT);
   if(start==null||end==null||end.isBefore(start)) throw new InvalidWorkflowException("Tax report period is invalid.");
   List<TaxPosting> records=taxPostings.findByCompanyCodeAndPostingDateRange(code,start.atStartOfDay(),end.plusDays(1).atStartOfDay());
   Map<String,List<TaxPosting>> grouped=new LinkedHashMap<>();
   for(TaxPosting p:records) grouped.computeIfAbsent(p.getTaxCode()+"|"+p.getInputOutput(),k->new ArrayList<>()).add(p);
   List<TaxReportLine> lines=new ArrayList<>();
   BigDecimal output=BigDecimal.ZERO,input=BigDecimal.ZERO,recoverable=BigDecimal.ZERO;
   for(List<TaxPosting> bucket:grouped.values()){
     TaxPosting first=bucket.get(0); TaxCode t=taxCodes.findByCompanyCodeAndTaxCodeIgnoreCase(code,first.getTaxCode()).orElse(null);
     BigDecimal base=bucket.stream().map(TaxPosting::getTaxableBase).reduce(BigDecimal.ZERO,BigDecimal::add);
     BigDecimal tax=bucket.stream().map(TaxPosting::getTaxAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
     BigDecimal rec=bucket.stream().map(TaxPosting::getRecoverableAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
     if("OUTPUT".equals(first.getInputOutput())) output=output.add(tax); else {input=input.add(tax);recoverable=recoverable.add(rec);}
     lines.add(new TaxReportLine(first.getTaxCode(),t==null?first.getTaxCode():t.getDescription(),t==null?BigDecimal.ZERO:t.getRate(),t!=null&&t.isWithholding(),first.getTaxType(),first.getJurisdiction(),first.getInputOutput(),base,tax,rec,"OUTPUT".equals(first.getInputOutput())?tax:rec.negate()));
   }
   return new TaxReportSummary(code,start,end,output,input,recoverable,output.subtract(recoverable),lines);
 }
}