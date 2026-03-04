package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestResponse {

    private UUID id;
    private LocalDate harvestDate;
    private Double kilogram;
    private HarvestStatus status;

}