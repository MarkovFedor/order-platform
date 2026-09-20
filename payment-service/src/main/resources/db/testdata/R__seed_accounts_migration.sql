INSERT INTO accounts (id, owner_name, balance, currency, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Alice', 1000.00, 'RUB', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222222222', 'Bob',    500.00, 'RUB', NOW(), NOW()),
    ('33333333-3333-3333-3333-333333333333', 'Carol',    0.00, 'RUB', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;