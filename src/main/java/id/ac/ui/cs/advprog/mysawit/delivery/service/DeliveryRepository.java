package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {

}
