package id.ac.ui.cs.advprog.mysawit.Repository;

import id.ac.ui.cs.advprog.mysawit.Model.Harvest;
import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            UUID buruhId,
            LocalDate startDate,
            LocalDate endDate,
            HarvestStatus status
    );
}