package id.ac.ui.cs.advprog.mysawit.garden.repository;

import id.ac.ui.cs.advprog.mysawit.garden.model.KebunSupir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KebunSupirRepository extends JpaRepository<KebunSupir, UUID> {

    boolean existsBySupirIdAndActiveTrue(UUID supirId);

    List<KebunSupir> findByKebunIdAndActiveTrue(UUID kebunId);
}
