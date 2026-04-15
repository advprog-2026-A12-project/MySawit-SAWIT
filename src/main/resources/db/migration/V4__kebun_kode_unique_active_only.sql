-- ini untuk bikin kode kebun bisa dipakai lagi kalau kebunnya sudah dihapus (is_active = false)
ALTER TABLE kebun DROP CONSTRAINT IF EXISTS kebun_kode_key;
DROP INDEX IF EXISTS idx_kebun_kode;

-- Cuman kebun yg aktid yg punya kode unik
CREATE UNIQUE INDEX idx_kebun_kode_active ON kebun(kode) WHERE is_active = TRUE;
