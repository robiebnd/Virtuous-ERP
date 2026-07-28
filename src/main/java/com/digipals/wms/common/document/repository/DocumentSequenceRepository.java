package com.digipals.wms.common.document.repository;

import com.digipals.wms.common.document.entity.DocumentSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface DocumentSequenceRepository
        extends JpaRepository<DocumentSequence, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DocumentSequence>
    findByDocumentTypeAndFinancialYear(
            String documentType,
            Integer financialYear);
}