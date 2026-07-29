CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.warehouse_products
(
    product_id UUID PRIMARY KEY,
    quantity   INTEGER NOT NULL,
    width      DOUBLE PRECISION,
    height     DOUBLE PRECISION,
    depth      DOUBLE PRECISION,
    weight     DOUBLE PRECISION,
    fragile    BOOLEAN
);