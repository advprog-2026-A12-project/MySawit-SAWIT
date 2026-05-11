package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class HarvestResponse {
    private UUID id;
    private UUID buruhId;
    private UUID mandorId;
    private LocalDate harvestDate;
    private Double kilogram;
    private HarvestStatus status;
    private String rejectionReason;    
    private Boolean bisaDiangkutTruk;
    private List<String> photos;
}