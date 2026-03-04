package id.ac.ui.cs.advprog.mysawit.garden.repository;

import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KebunRepository extends JpaRepository<Kebun, UUID> {

    boolean existsByKodeUnik(String kodeUnik);

}