package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.IntercompanyRequest;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class IntercompanyService {
 private final CompanyCodeRepository companies; private final IntercompanyTransactionRepository repository; private final FinancePostingService posting;
 public IntercompanyTransaction post(IntercompanyRequest r){
   String source=normalize(r.sourceCompanyCode()), target=normalize(r.targetCompanyCode()); if(source.equals(target))throw new InvalidWorkflowException("Source and target company codes must differ.");
   companies.findByCompanyCodeIgnoreCase(source).orElseThrow(()->new InvalidWorkflowException("Source company code not found."));
   companies.findByCompanyCodeIgnoreCase(target).orElseThrow(()->new InvalidWorkflowException("Target company code not found."));
   UUID ref=UUID.nameUUIDFromBytes(("IC:"+r.transactionNumber()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   var sourceLines=List.of(new FinancePostingService.PostingLine(r.sourceDebitAccountCode(),r.amount(),BigDecimal.ZERO,null,null,null,null,r.description()).withPartner(target),new FinancePostingService.PostingLine(r.sourceCreditAccountCode(),BigDecimal.ZERO,r.amount(),null,null,null,null,r.description()).withPartner(target));
   var targetLines=List.of(new FinancePostingService.PostingLine(r.targetDebitAccountCode(),r.amount(),BigDecimal.ZERO,null,null,null,null,r.description()).withPartner(source),new FinancePostingService.PostingLine(r.targetCreditAccountCode(),BigDecimal.ZERO,r.amount(),null,null,null,null,r.description()).withPartner(source));
   AccountingDocument sourceDoc=posting.postBalancedAtDate(source,"INTERCOMPANY","INTERCOMPANY",ref,r.transactionNumber(),r.currency(),r.description(),sourceLines,r.transactionDate().atStartOfDay());
   AccountingDocument targetDoc=posting.postBalancedAtDate(target,"INTERCOMPANY","INTERCOMPANY",UUID.nameUUIDFromBytes(("ICT:"+r.transactionNumber()).getBytes(java.nio.charset.StandardCharsets.UTF_8)),r.transactionNumber(),r.currency(),r.description(),targetLines,r.transactionDate().atStartOfDay());
   return repository.save(IntercompanyTransaction.builder().transactionNumber(r.transactionNumber().trim()).transactionDate(r.transactionDate()).sourceCompanyCode(source).targetCompanyCode(target).currency(r.currency().trim().toUpperCase()).amount(r.amount()).sourceAccountCode(r.sourceDebitAccountCode()).targetAccountCode(r.targetDebitAccountCode()).sourceDebitAccountCode(r.sourceDebitAccountCode()).sourceCreditAccountCode(r.sourceCreditAccountCode()).targetDebitAccountCode(r.targetDebitAccountCode()).targetCreditAccountCode(r.targetCreditAccountCode()).sourceDocumentId(sourceDoc.getId()).targetDocumentId(targetDoc.getId()).description(r.description()).status("POSTED").build());
 }
 public List<IntercompanyTransaction> list(){return repository.findByStatusOrderByTransactionDateDesc("POSTED");}
 private String normalize(String s){return s.trim().toUpperCase(Locale.ROOT);}
}