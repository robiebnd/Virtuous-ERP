package com.digipals.wms.integration.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationOutboxClaimService {

    private final IntegrationOutboxEventRepository repository;

    @Value("$" + "{wms.integration.outbox.batch-size:25}")
    private int batchSize;

    @Value("$" + "{wms.integration.outbox.stale-lock-minutes:10}")
    private int staleLockMinutes;

    @Transactional
    public List<UUID> claimBatch() {
        int safeBatchSize = Math.max(1, Math.min(batchSize, 200));
        int safeStaleLockMinutes = Math.max(1, staleLockMinutes);
        List<IntegrationOutboxEvent> events = repository.findClaimable(safeBatchSize, safeStaleLockMinutes);
        LocalDateTime now = LocalDateTime.now();
        events.forEach(event -> {
            event.setStatus(IntegrationOutboxStatus.PROCESSING);
            event.setLockedAt(now);
            event.setAttempts(event.getAttempts() + 1);
            event.setLastError(null);
        });
        repository.saveAll(events);
        return events.stream().map(IntegrationOutboxEvent::getId).toList();
    }
}
