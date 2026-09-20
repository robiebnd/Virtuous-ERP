package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BankTransactionRepository extends JpaRepository<BankTransaction,UUID>{
 Optional<BankTransaction> findByBankAccountIdAndTransactionNumber(UUID bankAccountId,String transactionNumber);
 List<BankTransaction> findByBankAccountIdOrderByTransactionDateDesc(UUID bankAccountId);
 List<BankTransaction> findByStatusOrderByTransactionDateDesc(String status);
 Optional<BankTransaction> findFirstByAccountingDocumentId(UUID accountingDocumentId);
}