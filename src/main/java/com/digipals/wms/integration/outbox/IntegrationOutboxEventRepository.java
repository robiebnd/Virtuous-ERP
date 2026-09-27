package com.digipals.wms.integration.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IntegrationOutboxEventRepository extends JpaRepository<IntegrationOutboxEvent, UUID> {

    @Query(value = """
            SELECT *
            FROM integration_outbox_events
            WHERE (
                (status IN ('PENDING', 'FAILED') AND available_at <= CURRENT_TIMESTAMP)
                OR (status = 'PROCESSING' AND locked_at < CURRENT_TIMESTAMP - make_interval(mins => :staleLockMinutes))
            )
            ORDER BY occurred_at, created_at, id
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<IntegrationOutboxEvent> findClaimable(
            @Param("limit") int limit,
            @Param("staleLockMinutes") int staleLockMinutes
    );
}
