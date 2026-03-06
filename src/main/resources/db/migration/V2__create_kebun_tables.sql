CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS kebun (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       nama VARCHAR(100) NOT NULL,
                       kode VARCHAR(50) NOT NULL UNIQUE,
                       luas_hektare DECIMAL(10,2) NOT NULL CHECK (luas_hektare > 0),
                       coord1_lat DECIMAL(10,7) NOT NULL,
                       coord1_lng DECIMAL(11,7) NOT NULL,
                       coord2_lat DECIMAL(10,7) NOT NULL,
                       coord2_lng DECIMAL(11,7) NOT NULL,
                       coord3_lat DECIMAL(10,7) NOT NULL,
                       coord3_lng DECIMAL(11,7) NOT NULL,
                       coord4_lat DECIMAL(10,7) NOT NULL,
                       coord4_lng DECIMAL(11,7) NOT NULL,
                       geom GEOMETRY(POLYGON, 4326) NOT NULL,
                       mandor_id UUID UNIQUE,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMPTZ DEFAULT NOW(),
                       updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS  idx_kebun_kode ON kebun(kode);

CREATE INDEX IF NOT EXISTS  idx_kebun_nama ON kebun(nama);

CREATE INDEX IF NOT EXISTS idx_kebun_geom
    ON kebun
        USING GIST (geom);

CREATE UNIQUE INDEX idx_kebun_mandor
    ON kebun(mandor_id)
    WHERE mandor_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS kebun_supir (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             kebun_id UUID NOT NULL REFERENCES kebun(id) ON DELETE CASCADE,
                             supir_id UUID NOT NULL,
                             is_active BOOLEAN DEFAULT TRUE,
                             assigned_at TIMESTAMPTZ DEFAULT NOW(),
                             unassigned_at TIMESTAMPTZ,
                             created_at TIMESTAMPTZ DEFAULT NOW(),
                             updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_ks_supir_active
    ON kebun_supir(supir_id)
    WHERE is_active = TRUE;

CREATE INDEX IF NOT EXISTS idx_ks_kebun_active
    ON kebun_supir(kebun_id)
    WHERE is_active = TRUE;