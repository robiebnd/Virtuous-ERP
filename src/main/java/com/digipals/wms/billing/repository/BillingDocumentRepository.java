package com.digipals.wms.billing.repository;

import com.digipals.wms.billing.entity.BillingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillingDocumentRepository extends JpaRepository<BillingDocument, UUID> {

    Optional<BillingDocument> findByBillingNumber(String billingNumber);

    Optional<BillingDocument> findByOutboundDeliveryId(UUID outboundDeliveryId);

    List<BillingDocument> findByCustomerCodeOrderByBillingDateDesc(String customerCode);
}
