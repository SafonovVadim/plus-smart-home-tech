CREATE SCHEMA IF NOT EXISTS shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_cart.carts (
    cart_id UUID PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    state VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart.cart_products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id UUID REFERENCES carts(cart_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL
);