CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.deliveries
(
    delivery_id     UUID PRIMARY KEY,
    order_id        UUID    NOT NULL,
    from_address    JSONB   NOT NULL,
    to_address      JSONB   NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile         BOOLEAN,
    status          VARCHAR NOT NULL CHECK (status IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELLED'))
);