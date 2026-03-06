package id.ac.ui.cs.advprog.mysawit.garden.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "kebun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kebun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false, unique = true, length = 50)
    private String kode;

    @Column(name = "luas_hektare", nullable = false)
    private Double luasHektare;

    @Column(name = "coord1_lat", nullable = false)
    private Double coord1Lat;

    @Column(name = "coord1_lng", nullable = false)
    private Double coord1Lng;

    @Column(name = "coord2_lat", nullable = false)
    private Double coord2Lat;

    @Column(name = "coord2_lng", nullable = false)
    private Double coord2Lng;

    @Column(name = "coord3_lat", nullable = false)
    private Double coord3Lat;

    @Column(name = "coord3_lng", nullable = false)
    private Double coord3Lng;

    @Column(name = "coord4_lat", nullable = false)
    private Double coord4Lat;

    @Column(name = "coord4_lng", nullable = false)
    private Double coord4Lng;

    @Column(name = "mandor_id")
    private UUID mandorId;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}