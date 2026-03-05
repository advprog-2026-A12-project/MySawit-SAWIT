package id.ac.ui.cs.advprog.mysawit.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "harvest_deliveries")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
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

   @NotNull
   @Column(name = "harvest_id", columnDefinition = "uuid")
   private String harvestId;

   @Column(name = "payload_kg", nullable = false)
   @Min(value = 1, message = "Muatan tidak boleh kosong")
   @Max(value = 400, message = "Muatan tidak boleh melebihi 400 Kg")
   private Double payloadKg;

   @Column(nullable = false, length = 50)
   private String status = "MEMUAT";

   @CreationTimestamp
   @Column(name = "created_at", updatable = false)
   private ZonedDateTime createdAt;

   @UpdateTimestamp
   @Column(name = "updated_at")
   private ZonedDateTime updatedAt;
}