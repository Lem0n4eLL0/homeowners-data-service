-- 1. PERSONAL_DATA
INSERT INTO homeowners_service.personal_data (id, account_id, first_name, last_name, surname, created_at, updated_at, version)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Иван', 'Иванов', 'Иванович', NOW(), NOW(), 1),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Петр', 'Петров', 'Петрович', NOW(), NOW(), 1),
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Анна', 'Сидорова', 'Алексеевна', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 2. COMPANIES
INSERT INTO homeowners_service.companies (id, name, created_at, updated_at, version)
VALUES 
    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'УК "Дом"', NOW(), NOW(), 1),
    ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'ТСЖ "Центральное"', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 3. PROPERTIES
INSERT INTO homeowners_service.properties (id, city, street, house_number, corpus, flat_number, created_at, updated_at, version)
VALUES 
    ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'Москва', 'ул. Ленина', '10', '1', '101', NOW(), NOW(), 1),
    ('10eebc99-9c0b-4ef8-bb6d-6bb9bd380a77', 'Москва', 'ул. Ленина', '10', '1', '102', NOW(), NOW(), 1),
    ('20eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 'Москва', 'ул. Мира', '25', NULL, '50', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 4. SERVICES
INSERT INTO homeowners_service.services (id, code, name, created_at, updated_at, version)
VALUES 
    ('30eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', 'COLD_WATER', 'Холодная вода', NOW(), NOW(), 1),
    ('40eebc99-9c0b-4ef8-bb6d-6bb9bd380aaa', 'ELECTRICITY', 'Электричество', NOW(), NOW(), 1),
    ('50eebc99-9c0b-4ef8-bb6d-6bb9bd380abb', 'HEATING', 'Отопление', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 5. PERSONAL_ACCOUNTS
INSERT INTO homeowners_service.personal_accounts (id, personal_number, property_id, company_id, service_id, created_at, updated_at, version)
VALUES 
    ('60eebc99-9c0b-4ef8-bb6d-6bb9bd380acc', 'PA-100001', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', '30eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', NOW(), NOW(), 1),
    ('70eebc99-9c0b-4ef8-bb6d-6bb9bd380add', 'PA-100002', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a77', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', '40eebc99-9c0b-4ef8-bb6d-6bb9bd380aaa', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 6. METERS
INSERT INTO homeowners_service.meters (id, serial_number, type, personal_account_id, created_at, updated_at, version)
VALUES 
    ('80eebc99-9c0b-4ef8-bb6d-6bb9bd380aee', 'MTR-001', 'WATER', '60eebc99-9c0b-4ef8-bb6d-6bb9bd380acc', NOW(), NOW(), 1),
    ('90eebc99-9c0b-4ef8-bb6d-6bb9bd380aff', 'MTR-002', 'ELECTRO', '70eebc99-9c0b-4ef8-bb6d-6bb9bd380add', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 7. METER_HISTORY_VALUES
INSERT INTO homeowners_service.meter_history_values (id, meter_id, value, date, created_at, updated_at, version)
VALUES 
    ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380b00', '80eebc99-9c0b-4ef8-bb6d-6bb9bd380aee', 125.5000, '2024-01-01', NOW(), NOW(), 1),
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', '80eebc99-9c0b-4ef8-bb6d-6bb9bd380aee', 130.7500, '2024-02-01', NOW(), NOW(), 1),
    ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '90eebc99-9c0b-4ef8-bb6d-6bb9bd380aff', 500.0000, '2024-01-01', NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 8. PROPERTY-MEMBERSHIPS
INSERT INTO homeowners_service.property_memberships (id, user_id, property_id, verification_status, verified_at, rejection_reason, created_at, updated_at, version)
VALUES 
    ('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380b33', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', true, NOW(), NULL, NOW(), NOW(), 1),
    ('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380b44', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a77', false, NULL, NULL, NOW(), NOW(), 1),
    ('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380b55', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', '20eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', true, NOW(), NULL, NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;