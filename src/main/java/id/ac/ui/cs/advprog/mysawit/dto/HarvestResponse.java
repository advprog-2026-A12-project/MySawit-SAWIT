package id.ac.ui.cs.advprog.mysawit.dto;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import lombok.*;

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