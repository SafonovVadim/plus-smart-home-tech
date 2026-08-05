CREATE SCHEMA IF NOT EXISTS "order";

CREATE TABLE IF NOT EXISTS "order".orders
(
    order_id         UUID PRIMARY KEY,
    shopping_cart_id UUID REFERENCES shopping_cart.carts (cart_id) ON DELETE SET NULL,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS "order".order_products
(
    id         UUID PRIMARY KEY,
    order_id   UUID    NOT NULL REFERENCES "order".orders (order_id) ON DELETE CASCADE,
    product_id UUID    NOT NULL REFERENCES shopping_store.products (product_id) ON DELETE CASCADE,
    quantity   INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS "order".order_bookings
(
    booking_id  UUID PRIMARY KEY,
    order_id    UUID NOT NULL REFERENCES "order".orders (order_id) ON DELETE CASCADE,
    product_id  UUID NOT NULL,
    quantity    INTEGER NOT NULL,
    delivery_id UUID
);