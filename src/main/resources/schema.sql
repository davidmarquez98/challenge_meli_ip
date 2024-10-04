CREATE TABLE IF NOT EXISTS ip_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(255),
    datetime TIMESTAMP,
    country VARCHAR(255),
    iso_code VARCHAR(10),
    languages VARCHAR(255),
    currency VARCHAR(50),
    currency_conversion VARCHAR(50),
    current_local_time VARCHAR(255),
    message_estimated_distance VARCHAR(255),
    estimated_distance DOUBLE,
    invocations VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_estimated_distance DOUBLE,
    invocations_count INTEGER
);
