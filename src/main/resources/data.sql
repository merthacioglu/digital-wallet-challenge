-- Insert test customers
-- password: SecurePassword123!
INSERT INTO customer (customer_id, name, surname, tckn, email, password, role)
VALUES (1001, 'John', 'Doe', '12345678901', 'john.doe@example.com',
        '$2a$10$PAkAwWnkw1r5jvidOQ3m9e6s.l3IB7Bz1Q2/pj.QoCBlCCzfacdSy', 'BASIC'),
       (1002, 'Jane', 'Smith', '10195827811', 'jane.smith@example.com',
        '$2a$10$PAkAwWnkw1r5jvidOQ3m9e6s.l3IB7Bz1Q2/pj.QoCBlCCzfacdSy', 'BASIC'),
       (1003, 'Admin', 'User', '98765432109', 'admin@example.com',
        '$2a$10$PAkAwWnkw1r5jvidOQ3m9e6s.l3IB7Bz1Q2/pj.QoCBlCCzfacdSy', 'ADMIN');

-- Insert test wallets
INSERT INTO wallet (id, wallet_id, wallet_name, currency, balance, usable_balance, active_for_shopping,
                    active_for_withdraw, customer_id)
VALUES (555, 'ebed7406-0593-4e01-bd7b-7f5abee2315f', 'Johns Main Wallet', 'TRY', 5000.00, 3500.00, true, true, 1001),
       (556, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Johns Savings', 'TRY', 10000.00, 10000.00, false, true, 1001),
       (557, 'f7e8d9c0-b1a2-3456-7890-1234567890ab', 'Janes Wallet', 'TRY', 2000.00, 1500.00, true, true, 1002),
       (558, '12345678-1234-1234-1234-123456789012', 'Admin Wallet', 'TRY', 100000.00, 100000.00, true, true, 1003);

-- Insert test transactions
INSERT INTO transaction (id, transaction_id, wallet_id, opposite_party, opposite_party_type, type, status, amount)
VALUES (601, 'ABCDEFGHIJ00', 555, 'TR330006100519786457841326', 'IBAN', 'DEPOSIT', 'APPROVED', 500.00),
       (602, 'ABCDEFGHIJ01', 555, 'TR330006100519786457841326', 'IBAN', 'WITHDRAW', 'PENDING', 1500.00),
       (603, 'ABCDEFGHIJ02', 556, 'PAY-12345', 'PAYMENT', 'DEPOSIT', 'APPROVED', 750.00),
       (604, 'ABCDEFGHIJ03', 557, 'TR440006100519786457841327', 'IBAN', 'DEPOSIT', 'PENDING', 2500.00);