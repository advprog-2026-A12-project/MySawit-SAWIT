CREATE TABLE IF NOT EXISTS harvests (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          buruh_id UUID NOT NULL,
                          harvest_date DATE,
                          kilogram DOUBLE PRECISION,
                          report_note TEXT,
                          status VARCHAR(50) DEFAULT 'PENDING',
                          rejection_reason TEXT,
                          CONSTRAINT unique_daily_harvest UNIQUE (buruh_id, harvest_date)
);

CREATE TABLE IF NOT EXISTS harvest_photos (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                harvest_id UUID NOT NULL REFERENCES harvests(id) ON DELETE CASCADE,
                                file_url TEXT NOT NULL
);