CREATE TABLE IF NOT EXISTS harvest_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supir_id UUID NOT NULL,
    supir_name VARCHAR(255),
    mandor_id UUID NOT NULL,
    harvest_id UUID NOT NULL REFERENCES harvests(id) ON DELETE CASCADE,
    payload_kg DOUBLE PRECISION NOT NULL CHECK (payload_kg >= 1 AND payload_kg <= 400),
    status VARCHAR(50) DEFAULT 'MEMUAT',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_deliveries_supir ON harvest_deliveries(supir_id);
CREATE INDEX idx_deliveries_mandor ON harvest_deliveries(mandor_id);