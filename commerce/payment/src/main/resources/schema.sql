CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments
(
    payment_id    UUID PRIMARY KEY,
    order_id      UUID  NOT NULL REFERENCES "order".orders (order_id) ON DELETE CASCADE,
    product_cost  DOUBLE PRECISION NOT NULL,
    delivery_cost DOUBLE PRECISION NOT NULL,
    total_cost    DOUBLE PRECISION NOT NULL,
    status        VARCHAR NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);