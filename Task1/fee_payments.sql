CREATE DATABASE college_fees;

USE college_fees;

CREATE TABLE fee_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reg_no VARCHAR(30) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
