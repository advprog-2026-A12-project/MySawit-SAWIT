package id.ac.ui.cs.advprog.mysawit.delivery.controller;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.delivery.service.DeliveryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

   private final DeliveryService deliveryService;

   @PostMapping
   public ResponseEntity<?> createDelivery(@RequestBody CreateDeliveryRequest request, HttpServletRequest req) {
      String role = (String) req.getAttribute("userRole");
      if (!"MANDOR".equals(role)) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak. Hanya Mandor yang dapat membuat penugasan.");
      }

      UUID mandorId = (UUID) req.getAttribute("userId");
      String mandorName = (String) req.getAttribute("userName");

      Delivery created = deliveryService.createDelivery(request, mandorId, mandorName);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
   }

   @GetMapping
   public ResponseEntity<?> getDeliveries(HttpServletRequest req) {
      String role = (String) req.getAttribute("userRole");
      UUID userId = (UUID) req.getAttribute("userId");

      if (role == null) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token tidak valid atau tidak ada.");
      }

      List<Delivery> deliveries = deliveryService.getDeliveriesByRole(userId, role);
      return ResponseEntity.ok(deliveries);
   }

   @PatchMapping("/{id}/status")
   public ResponseEntity<?> updateStatus(
           @PathVariable UUID id,
           @RequestParam String status,
           HttpServletRequest req) {

      String role = (String) req.getAttribute("userRole");
      if (!"SUPIR_TRUK".equals(role)) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak. Hanya Supir Truk yang dapat mengubah status.");
      }

      Delivery updated = deliveryService.updateStatus(id, status);
      return ResponseEntity.ok(updated);
   }

   @PatchMapping("/{id}/mandor-approval")
   public ResponseEntity<?> approveByMandor(
           @PathVariable UUID id,
           @RequestParam boolean isApproved,
           @RequestParam(required = false) String rejectionReason,
           HttpServletRequest req) {

      String role = (String) req.getAttribute("userRole");
      if (!"MANDOR".equals(role)) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak. Membutuhkan role MANDOR.");
      }

      Delivery updated = deliveryService.mandorApprove(id, isApproved, rejectionReason);
      return ResponseEntity.ok(updated);
   }

   @PatchMapping("/{id}/admin-approval")
   public ResponseEntity<?> approveByAdmin(
           @PathVariable UUID id,
           @RequestParam boolean isApproved,
           @RequestParam(required = false) Double approvedPayloadKg,
           @RequestParam(required = false) String rejectionReason,
           HttpServletRequest req) {

      String role = (String) req.getAttribute("userRole");
      if (!"ADMIN".equals(role)) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak. Membutuhkan role ADMIN.");
      }

      Delivery updated = deliveryService.adminApprove(id, isApproved, approvedPayloadKg, rejectionReason);
      return ResponseEntity.ok(updated);
   }
}