package id.ac.ui.cs.advprog.mysawit.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "harvest_deliveries")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Delivery {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private String id;

   @Column(nullable = false)
   private String supirName;

   @Column(nullable = false)
   @Min(value = 1, message = "Muatan tidak boleh kosong")
   @Max(value = 400, message = "Muatan tidak boleh melebihi 400 Kg")
   private Double payloadKg;

   @Column(nullable = false)
   private String status = "MEMUAT";

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }
}