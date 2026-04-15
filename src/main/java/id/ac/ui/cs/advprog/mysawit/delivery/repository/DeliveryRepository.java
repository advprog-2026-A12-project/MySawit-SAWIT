package id.ac.ui.cs.advprog.mysawit.delivery.repository;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
   List<Delivery> findBySupirId(UUID supirId);

   List<Delivery> findByMandorId(UUID mandorId);

   List<Delivery> findByStatus(String status);
}