package com.digipals.wms.integration.outbox;

import com.digipals.wms.integration.IntegrationEventTypes;
import com.digipals.wms.integration.o2c.CustomerBillingPostedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IntegrationOutboxServiceTest {

    @Mock
    private IntegrationOutboxEventRepository repository;

    @Test
    void enqueuePersistsSerializedPendingEvent() {
        IntegrationOutboxService service = new IntegrationOutboxService(repository, new ObjectMapper());
        UUID id = UUID.randomUUID();
        LocalDateTime postingDate = LocalDateTime.of(2026, 9, 28, 10, 0);

        service.enqueue(
                IntegrationEventTypes.O2C_CUSTOMER_BILLING_POSTED,
                "BILLING_DOCUMENT",
                id,
                new CustomerBillingPostedEvent(id, "INV-1001", "USD", new BigDecimal("100.00"), postingDate),
                postingDate
        );

        var captor = org.mockito.ArgumentCaptor.forClass(IntegrationOutboxEvent.class);
        verify(repository).save(captor.capture());

        IntegrationOutboxEvent event = captor.getValue();
        assertEquals(IntegrationEventTypes.O2C_CUSTOMER_BILLING_POSTED, event.getEventType());
        assertEquals("BILLING_DOCUMENT", event.getAggregateType());
        assertEquals(id, event.getAggregateId());
        assertEquals(IntegrationOutboxStatus.PENDING, event.getStatus());
        assertEquals(0, event.getAttempts());
        assertEquals(postingDate, event.getOccurredAt());
        assertTrue(event.getPayload().contains("INV-1001"));
        assertTrue(event.getPayload().contains("100.00"));
        assertNotNull(event.getAvailableAt());
    }
}
