CREATE TABLE rate_limit_rules (
    rule_id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(client_id) ON DELETE CASCADE,
    plan_id BIGINT REFERENCES plans(plan_id) ON DELETE CASCADE,
    requests_per_minute INT NOT NULL,
    requests_per_hour INT NOT NULL,
    daily_quota INT NOT NULL,
    algorithm VARCHAR(50) NOT NULL DEFAULT 'SLIDING_WINDOW'
);
