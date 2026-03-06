package id.ac.ui.cs.advprog.mysawit.harvest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "harvests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "buruh_id", nullable = false)
    private UUID buruhId;

    @Column(nullable = false)
    private LocalDate harvestDate;

    @Column(nullable = false)
    private Double kilogram;

    private String reportNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HarvestStatus status;

    private String rejectionReason;

    @OneToMany(
            mappedBy = "harvest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<HarvestPhoto> photos;
}