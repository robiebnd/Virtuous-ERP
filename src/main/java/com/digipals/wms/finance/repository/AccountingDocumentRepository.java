package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.AccountingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountingDocumentRepository extends JpaRepository<AccountingDocument, UUID> {
    Optional<AccountingDocument> findByDocumentNumber(String documentNumber);
    Optional<AccountingDocument> findFirstByReferenceTypeAndReferenceIdAndStatus(String referenceType, UUID referenceId, String status);
    List<AccountingDocument> findAllByOrderByPostingDateDesc();
    List<AccountingDocument> findByPostingDateBetweenOrderByPostingDateDesc(LocalDateTime from, LocalDateTime to);
}
