package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BankAccountRepository extends JpaRepository<BankAccount,UUID>{ List<BankAccount> findByActiveTrueOrderByBankNameAsc(); }