package id.ac.ui.cs.advprog.mysawit.harvest.repository;

import id.ac.ui.cs.advprog.mysawit.harvest.model.Harvest;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestRepository extends JpaRepository<Harvest, UUID> {

    boolean existsByBuruhIdAndHarvestDate(UUID buruhId, LocalDate harvestDate);

    @Query("""
    SELECT h FROM Harvest h
    WHERE h.buruhId = :buruhId
    AND (:startDate IS NULL OR h.harvestDate >= :startDate)
    AND (:endDate IS NULL OR h.harvestDate <= :endDate)
    AND (:status IS NULL OR h.status = :status)
""")
    List<Harvest> findWithFilter(
            @Param("buruhId") UUID buruhId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") HarvestStatus status
    );
}