package com.digipals.wms.integration.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationOutboxProcessor {

    private final IntegrationOutboxEventRepository repository;
    private final FinanceIntegrationOutboxHandler handler;

    @Transactional
    public void process(UUID eventId) {
        IntegrationOutboxEvent event = repository.findById(eventId).orElse(null);
        if (event == null || event.getStatus() != IntegrationOutboxStatus.PROCESSING) return;

        handler.handle(event);

        event.setStatus(IntegrationOutboxStatus.PROCESSED);
        event.setProcessedAt(LocalDateTime.now());
        event.setLockedAt(null);
        event.setLastError(null);
        repository.save(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID eventId, Exception ex) {
        IntegrationOutboxEvent event = repository.findById(eventId).orElse(null);
        if (event == null) return;

        int attempts = Math.max(1, event.getAttempts());
        long delaySeconds = Math.min(900L, 5L * (1L << Math.min(attempts - 1, 8)));
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        if (message.length() > 2000) message = message.substring(0, 2000);

        event.setStatus(IntegrationOutboxStatus.FAILED);
        event.setLockedAt(null);
        event.setAvailableAt(LocalDateTime.now().plusSeconds(delaySeconds));
        event.setLastError(message);
        repository.save(event);
    }
}
