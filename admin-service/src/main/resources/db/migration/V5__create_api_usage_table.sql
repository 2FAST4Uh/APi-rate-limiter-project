CREATE TABLE api_usage (
    usage_id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(client_id) ON DELETE SET NULL,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INT NOT NULL,
    response_time_ms BIGINT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_usage_client_timestamp ON api_usage(client_id, timestamp);
CREATE INDEX idx_api_usage_endpoint ON api_usage(endpoint);
CREATE INDEX idx_api_usage_status_code ON api_usage(status_code);
