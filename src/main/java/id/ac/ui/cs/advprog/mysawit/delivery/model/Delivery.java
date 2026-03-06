package id.ac.ui.cs.advprog.mysawit.delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;

@Entity
@Table(name = "harvest_deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(columnDefinition = "uuid")
   private String id;

   @NotNull
   @Column(name = "supir_id", columnDefinition = "uuid")
   private String supirId;

   @Column(name = "supir_name")
   private String supirName;

   @NotNull
   @Column(name = "mandor_id", columnDefinition = "uuid")
   private String mandorId;

   @Column(name = "mandor_name")
   private String mandorName;

   @NotNull
   @Column(name = "harvest_id", columnDefinition = "uuid")
   private String harvestId;

   @Column(name = "payload_kg", nullable = false)
   @Min(value = 1, message = "Muatan tidak boleh kosong")
   @Max(value = 400, message = "Muatan tidak boleh melebihi 400 Kg")
   private Double payloadKg;

   @Column(name = "approved_payload_kg")
   private Double approvedPayloadKg;

   @Column(nullable = false, length = 50)
   private String status = "MEMUAT";

   @Column(name = "rejection_reason", columnDefinition = "TEXT")
   private String rejectionReason;

   @CreationTimestamp
   @Column(name = "created_at", updatable = false)
   private ZonedDateTime createdAt;

   @Column(name = "sent_at")
   private ZonedDateTime sentAt;

   @Column(name = "arrived_at")
   private ZonedDateTime arrivedAt;

   @UpdateTimestamp
   @Column(name = "updated_at")
   private ZonedDateTime updatedAt;
}