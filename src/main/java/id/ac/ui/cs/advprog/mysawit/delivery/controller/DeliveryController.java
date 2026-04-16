package id.ac.ui.cs.advprog.mysawit.delivery.controller;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request,
            @RequestAttribute("role") String role,
            @RequestAttribute("userId") UUID userId,
            @RequestAttribute(value = "userName", required = false) String userName) {

        if (!"MANDOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: hanya Mandor yang bisa membuat pengiriman");
        }

        try {
            Delivery created = deliveryService.createDelivery(request, userId, userName != null ? userName : "Mandor");
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getDeliveries(
            @RequestAttribute(value = "role", required = false) String role,
            @RequestAttribute(value = "userId", required = false) UUID userId,
            @RequestParam(required = false) String supirName) {

        if (role == null || userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if ("MANDOR".equals(role) && supirName != null) {
            return ResponseEntity.ok(deliveryService.getDeliveriesByMandorFiltered(userId, supirName));
        }

        List<Delivery> deliveries = deliveryService.getDeliveriesByRole(userId, role);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/supir-tasks")
    public ResponseEntity<?> getSupirTasks(
            @RequestAttribute("role") String role,
            @RequestAttribute("userId") UUID userId) {

        if (!"SUPIR_TRUK".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: hanya Supir Truk yang bisa melihat daftar tugasnya");
        }

        List<Delivery> tasks = deliveryService.getDeliveriesBySupirId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> advanceStatus(
            @PathVariable UUID id,
            @RequestAttribute("role") String role) {

        if (!"SUPIR_TRUK".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: hanya Supir Truk yang bisa mengubah status");
        }

        try {
            Delivery updated = deliveryService.advanceStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/mandor-approval")
    public ResponseEntity<?> approveByMandor(
            @PathVariable UUID id,
            @RequestParam boolean isApproved,
            @RequestParam(required = false) String rejectionReason,
            @RequestAttribute("role") String role) {

        if (!"MANDOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
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
            @RequestAttribute("role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        Delivery updated = deliveryService.adminApprove(id, isApproved, approvedPayloadKg, rejectionReason);
        return ResponseEntity.ok(updated);
    }
}