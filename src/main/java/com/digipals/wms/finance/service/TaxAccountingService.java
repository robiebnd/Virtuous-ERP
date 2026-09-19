package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.TaxPostingRequest;
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
   BigDecimal base=r.taxableAmount().setScale(2,RoundingMode.HALF_UP), amount=base.multiply(tax.getRate()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
   String taxAccount=tax.getTaxAccountCode()!=null&&!tax.getTaxAccountCode().isBlank()?tax.getTaxAccountCode():(direction.equals("OUTPUT")?tax.getOutputAccountCode():tax.getInputAccountCode());
   if(taxAccount==null||taxAccount.isBlank()) throw new InvalidWorkflowException("Tax account is not configured for tax code "+tax.getTaxCode()+".");
   BigDecimal gross=base.add(amount);
   List<FinancePostingService.PostingLine> lines;
   if(direction.equals("OUTPUT")) lines=List.of(new FinancePostingService.PostingLine(r.baseDebitAccount(),gross,BigDecimal.ZERO,null,null,null,null,"Customer tax-inclusive receivable"),new FinancePostingService.PostingLine(r.baseCreditAccount(),BigDecimal.ZERO,base,null,null,null,null,"Revenue excluding output tax"),new FinancePostingService.PostingLine(taxAccount,BigDecimal.ZERO,amount,null,null,null,null,"Output VAT / sales tax"));
   else lines=List.of(new FinancePostingService.PostingLine(r.baseDebitAccount(),base,BigDecimal.ZERO,null,null,null,null,"Expense / inventory excluding input tax"),new FinancePostingService.PostingLine(taxAccount,amount,BigDecimal.ZERO,null,null,null,null,"Recoverable input VAT / sales tax"),new FinancePostingService.PostingLine(r.baseCreditAccount(),BigDecimal.ZERO,gross,null,null,null,null,"Supplier payable tax-inclusive"));
   UUID ref=UUID.nameUUIDFromBytes(("TAX:"+company+":"+r.referenceNumber()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   AccountingDocument doc=posting.postBalancedAtDate(company,r.documentType(),"TAX_DOCUMENT",ref,r.referenceNumber(),r.currency(),r.description(),lines,r.postingDate().atStartOfDay());
   return taxPostings.save(TaxPosting.builder().accountingDocument(doc).companyCode(company).taxCode(tax.getTaxCode()).taxType(tax.getTaxType()).jurisdiction(tax.getJurisdiction()).taxableBase(base).taxAmount(amount).inputOutput(direction).taxAccountCode(taxAccount).recoverableAmount(direction.equals("INPUT")?amount.multiply((tax.getRecoverablePercent()==null?BigDecimal.valueOf(100):tax.getRecoverablePercent())).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP):BigDecimal.ZERO).build());
 }
 public List<TaxPosting> list(String company){return taxPostings.findByCompanyCodeOrderByCreatedAtDesc(company.trim().toUpperCase(Locale.ROOT));}
}