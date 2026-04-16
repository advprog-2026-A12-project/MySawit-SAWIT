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

    boolean existsByMandorIdAndActiveTrue(UUID mandorId);

    Optional<Kebun> findByIdAndActiveTrue(UUID id);

    @Query("SELECT k FROM Kebun k WHERE k.active = true "
            + "AND (:nama IS NULL OR LOWER(k.nama) LIKE LOWER(CONCAT('%', CAST(:nama AS string), '%'))) "
            + "AND (:kode IS NULL OR LOWER(k.kode) LIKE LOWER(CONCAT('%', CAST(:kode AS string), '%')))")
    List<Kebun> searchActiveKebun(@Param("nama") String nama, @Param("kode") String kode);

        @Query(value = "SELECT EXISTS ("
            + "SELECT 1 FROM kebun k "
            + "WHERE k.is_active = TRUE "
            + "AND (:excludeId IS NULL OR k.id <> :excludeId) "
            + "AND ST_Intersects("
            + "ST_GeomFromText(:candidatePolygonWkt, 4326),"
            + "ST_GeomFromText("
            + "'POLYGON((' || "
            + "k.coord1_lng || ' ' || k.coord1_lat || ',' || "
            + "k.coord2_lng || ' ' || k.coord2_lat || ',' || "
            + "k.coord3_lng || ' ' || k.coord3_lat || ',' || "
            + "k.coord4_lng || ' ' || k.coord4_lat || ',' || "
            + "k.coord1_lng || ' ' || k.coord1_lat || '))', 4326)"
            + ")"
            + ")", nativeQuery = true)
        boolean existsOverlappingActiveKebun(
            @Param("candidatePolygonWkt") String candidatePolygonWkt,
            @Param("excludeId") UUID excludeId
        );
}