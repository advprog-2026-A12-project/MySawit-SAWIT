package id.ac.ui.cs.advprog.mysawit.controller;

import id.ac.ui.cs.advprog.mysawit.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
