package id.ac.ui.cs.advprog.mysawit.harvest.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "harvests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "buruh_id", nullable = false)
    private UUID buruhId;

    @Column(name = "mandor_id", nullable = false)
    private UUID mandorId;

    @Column(nullable = false)
    private LocalDate harvestDate;

    @Column(nullable = false)
    private Double kilogram;

    private String reportNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HarvestStatus status;

    private String rejectionReason;

    @Column(nullable = false)
    @Builder.Default
    private Boolean bisaDiangkutTruk = false;

    @Column(name = "actioned_by_mandor_id")
    private UUID actionedByMandorId;

    @OneToMany(
            mappedBy = "harvest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<HarvestPhoto> photos;
}