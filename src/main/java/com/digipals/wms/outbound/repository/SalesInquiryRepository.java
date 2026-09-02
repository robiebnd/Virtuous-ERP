package com.digipals.wms.outbound.repository;

import com.digipals.wms.outbound.entity.SalesInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalesInquiryRepository extends JpaRepository<SalesInquiry, UUID> {
    Optional<SalesInquiry> findByInquiryNumber(String inquiryNumber);
}
