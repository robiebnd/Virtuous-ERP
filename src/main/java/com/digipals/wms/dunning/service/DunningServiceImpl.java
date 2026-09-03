package com.digipals.wms.dunning.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.dunning.dto.CreateDunningRequest;
import com.digipals.wms.dunning.entity.DunningCase;
import com.digipals.wms.dunning.entity.DunningStatus;
import com.digipals.wms.dunning.repository.DunningCaseRepository;
import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.repository.PaymentAllocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DunningServiceImpl implements DunningService {

    private final DunningCaseRepository dunningRepository;
    private final BillingDocumentRepository billingRepository;
    private final PaymentAllocationRepository allocationRepository;

    @Override
    public DunningCase create(CreateDunningRequest request) {
        BillingDocument billing = billingRepository.findById(request.billingDocumentId())
                .orElseThrow(() -> new EntityNotFoundException("Billing document not found: " + request.billingDocumentId()));

        if (billing.getStatus() != BillingStatus.POSTED) {
            throw new IllegalStateException("Dunning can only be created for a posted billing document");
        }

        BigDecimal applied = allocationRepository.findByBillingDocumentId(billing.getId()).stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = billing.getTotalAmount().subtract(applied).max(BigDecimal.ZERO);

        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Billing document has no outstanding balance");
        }

        DunningCase existing = dunningRepository
                .findByBillingDocumentIdAndStatusNot(billing.getId(), DunningStatus.CANCELLED)
                .orElse(null);
        if (existing != null && existing.getStatus() != DunningStatus.RESOLVED) {
            throw new IllegalStateException("An active dunning case already exists for this billing document");
        }

        int level = request.dunningLevel() == null ? 1 : request.dunningLevel();
        if (level < 1) throw new IllegalArgumentException("Dunning level must be at least 1");

        DunningCase dunning = DunningCase.builder()
                .dunningNumber("DUN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT))
                .billingDocument(billing)
                .customerCode(billing.getCustomerCode())
                .currency(billing.getCurrency())
                .outstandingAmount(outstanding)
                .dueDate(billing.getBillingDate())
                .dunningDate(LocalDateTime.now())
                .dunningLevel(level)
                .status(DunningStatus.OPEN)
                .message(request.message())
                .build();

        return dunningRepository.save(dunning);
    }

    @Override
    public DunningCase send(UUID id) {
        DunningCase dunning = findById(id);
        if (dunning.getStatus() != DunningStatus.OPEN) {
            throw new IllegalStateException("Only open dunning cases can be sent");
        }
        dunning.setStatus(DunningStatus.SENT);
        dunning.setDunningDate(LocalDateTime.now());
        return dunningRepository.save(dunning);
    }

    @Override
    public DunningCase resolve(UUID id) {
        DunningCase dunning = findById(id);
        if (dunning.getStatus() == DunningStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled dunning case cannot be resolved");
        }
        dunning.setStatus(DunningStatus.RESOLVED);
        return dunningRepository.save(dunning);
    }

    @Override
    @Transactional(readOnly = true)
    public DunningCase findById(UUID id) {
        return dunningRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dunning case not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DunningCase> findAll() {
        return dunningRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DunningCase> findByCustomerCode(String customerCode) {
        return dunningRepository.findByCustomerCodeOrderByDunningDateDesc(customerCode);
    }
}
