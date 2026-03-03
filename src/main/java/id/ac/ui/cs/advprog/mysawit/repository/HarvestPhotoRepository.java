package id.ac.ui.cs.advprog.mysawit.Repository;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HarvestPhotoRepository extends JpaRepository<HarvestPhoto, UUID> {

    List<HarvestPhoto> findByHarvestId(UUID harvestId);
}