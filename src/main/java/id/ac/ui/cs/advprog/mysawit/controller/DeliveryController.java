package id.ac.ui.cs.advprog.mysawit.Controller;

import id.ac.ui.cs.advprog.mysawit.Model.Delivery;
import id.ac.ui.cs.advprog.mysawit.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

   @Autowired
   private DeliveryRepository deliveryRepository;

   @GetMapping
   public ResponseEntity<List<Delivery>> getAllDeliveries() {
      return ResponseEntity.ok(deliveryRepository.findAll());
   }

   @PostMapping
   public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
      Delivery saved = deliveryRepository.save(delivery);
      return ResponseEntity.ok(saved);
   }
}
