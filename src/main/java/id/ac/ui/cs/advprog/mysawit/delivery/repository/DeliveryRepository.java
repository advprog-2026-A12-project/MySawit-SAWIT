package id.ac.ui.cs.advprog.mysawit.delivery.repository;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {
   List<Delivery> findBySupirId(String supirId);
   List<Delivery> findByMandorId(String mandorId);
}