ALTER TABLE harvest_deliveries
    ADD COLUMN IF NOT EXISTS mandor_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS approved_payload_kg DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS rejection_reason TEXT,
    ADD COLUMN IF NOT EXISTS sent_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS arrived_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_deliveries_created_at ON harvest_deliveries(created_at);