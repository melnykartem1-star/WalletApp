CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    locale VARCHAR(10) DEFAULT 'en-GB',
    timezone VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    last_logon TIMESTAMP NOT NULL
);

CREATE TABLE accounts(
    account_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) NOT NULL ON DELETE CASCADE,
    balance NUMERIC(19, 4) DEFAULT 0.0000,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    currency VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE categories(
    category_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) NOT NULL ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    color VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    icon VARCHAR(255)
);

CREATE TABLE merchants(
    merchant_id BIGSERIAL PRIMARY KEY,
    default_category_id BIGINT REFERENCES categories(category_id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES users(user_id) NOT NULL ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    icon VARCHAR(255)
);

CREATE TABLE transactions(
    transaction_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(account_id) NOT NULL,
    target_account_id BIGINT REFERENCES accounts(account_id),
    category_id BIGINT REFERENCES categories(category_id) ON DELETE SET NULL,
    merchant_id BIGINT REFERENCES merchants(merchant_id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 4) DEFAULT 0.0000,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_tr_page ON transactions(account_id, created_at DESC);
CREATE INDEX idx_cat_page ON categories(user_id, title ASC);