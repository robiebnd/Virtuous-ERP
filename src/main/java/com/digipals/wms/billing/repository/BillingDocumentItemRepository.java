package com.digipals.wms.billing.repository;

import com.digipals.wms.billing.entity.BillingDocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillingDocumentItemRepository extends JpaRepository<BillingDocumentItem, UUID> {
}
