ALTER TABLE harvests
    ADD COLUMN IF NOT EXISTS mandor_id UUID NOT NULL,
    ADD COLUMN IF NOT EXISTS bisa_diangkut_truk BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS actioned_by_mandor_id UUID;

ALTER TABLE harvests ALTER COLUMN harvest_date SET NOT NULL;
ALTER TABLE harvests ALTER COLUMN kilogram SET NOT NULL;
ALTER TABLE harvests ALTER COLUMN status SET NOT NULL;

CREATE TABLE IF NOT EXISTS harvest_photos (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    harvest_id UUID NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_harvest_photo_harvest
    FOREIGN KEY (harvest_id)
    REFERENCES harvests(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_harvest_photo_harvest ON harvest_photos(harvest_id);