package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.GlAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GlAccountRepository extends JpaRepository<GlAccount, UUID> {
    Optional<GlAccount> findByAccountCode(String accountCode);
    List<GlAccount> findAllByActiveTrueOrderByAccountCode();
}
