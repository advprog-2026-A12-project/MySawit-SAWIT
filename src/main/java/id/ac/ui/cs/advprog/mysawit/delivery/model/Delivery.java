package id.ac.ui.cs.advprog.mysawit.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.ElementCollection;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "harvest_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID supirId;

    private String supirName;

    @NotNull
    private UUID mandorId;

    private String mandorName;

    @ElementCollection
    private List<UUID> harvestIds;

    @Min(1)
    @Max(400)
    private Double payloadKg;

    private Double approvedPayloadKg;

    @Builder.Default
    private String status = "MEMUAT";

    @Builder.Default
    private String approvalStatus = "PENDING";

    private String rejectionReason;

    private LocalDateTime sentAt;

    private LocalDateTime arrivedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime tanggal;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}