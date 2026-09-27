package com.digipals.wms.integration.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationOutboxService {

    private final IntegrationOutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void enqueue(String eventType, String aggregateType, UUID aggregateId, Object event, LocalDateTime occurredAt) {
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("Integration event type is required.");
        if (aggregateType == null || aggregateType.isBlank()) throw new IllegalArgumentException("Integration aggregate type is required.");
        if (aggregateId == null) throw new IllegalArgumentException("Integration aggregate ID is required.");
        if (event == null) throw new IllegalArgumentException("Integration event payload is required.");

        final String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize integration event " + eventType + ".", ex);
        }

        LocalDateTime now = LocalDateTime.now();
        repository.save(IntegrationOutboxEvent.builder()
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .occurredAt(occurredAt == null ? now : occurredAt)
                .availableAt(now)
                .status(IntegrationOutboxStatus.PENDING)
                .attempts(0)
                .build());
    }
}
