-- File sementara
-- untuk sementara geom bisa dibuat nullable krn skrng aku belum implement trigger untuk auto compute geom dari koordinat (utk overlap validation nanti)
ALTER TABLE kebun ALTER COLUMN geom DROP NOT NULL;
