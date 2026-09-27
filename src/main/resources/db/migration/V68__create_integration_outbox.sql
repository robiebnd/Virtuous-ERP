CREATE TABLE integration_outbox_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(120) NOT NULL,
    aggregate_type VARCHAR(80) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload TEXT NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    available_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempts INTEGER NOT NULL DEFAULT 0,
    locked_at TIMESTAMP NULL,
    processed_at TIMESTAMP NULL,
    last_error VARCHAR(2000) NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    CONSTRAINT uq_outbox_event_aggregate UNIQUE (event_type, aggregate_type, aggregate_id)
);

CREATE INDEX idx_outbox_status_available
    ON integration_outbox_events (status, available_at);

CREATE INDEX idx_outbox_processing_lock
    ON integration_outbox_events (status, locked_at);

CREATE INDEX idx_outbox_aggregate
    ON integration_outbox_events (aggregate_type, aggregate_id);
