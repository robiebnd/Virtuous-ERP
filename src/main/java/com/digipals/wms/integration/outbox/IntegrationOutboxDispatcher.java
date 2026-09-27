package com.digipals.wms.integration.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationOutboxDispatcher {

    private final IntegrationOutboxClaimService claimService;
    private final IntegrationOutboxProcessor processor;

    @Value("$" + "{wms.integration.outbox.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelayString = "$" + "{wms.integration.outbox.fixed-delay-ms:5000}")
    public void dispatch() {
        if (!enabled) return;

        List<UUID> eventIds = claimService.claimBatch();
        for (UUID eventId : eventIds) {
            try {
                processor.process(eventId);
            } catch (Exception ex) {
                log.error("Integration outbox event {} failed; scheduling retry.", eventId, ex);
                try {
                    processor.markFailed(eventId, ex);
                } catch (Exception failureUpdateException) {
                    log.error("Unable to mark integration outbox event {} as failed; stale-lock recovery will retry it.", eventId, failureUpdateException);
                }
            }
        }
    }
}
