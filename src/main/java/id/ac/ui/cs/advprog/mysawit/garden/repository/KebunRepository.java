package id.ac.ui.cs.advprog.mysawit.garden.repository;

import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KebunRepository extends JpaRepository<Kebun, UUID> {

    boolean existsByKodeAndActiveTrue(String kode);

    Optional<Kebun> findByIdAndActiveTrue(UUID id);

    @Query("SELECT k FROM Kebun k WHERE k.active = true "
            + "AND (:nama IS NULL OR LOWER(k.nama) LIKE LOWER(CONCAT('%', CAST(:nama AS string), '%'))) "
            + "AND (:kode IS NULL OR LOWER(k.kode) LIKE LOWER(CONCAT('%', CAST(:kode AS string), '%')))")
    List<Kebun> searchActiveKebun(@Param("nama") String nama, @Param("kode") String kode);
}