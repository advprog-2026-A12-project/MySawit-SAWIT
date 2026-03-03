package id.ac.ui.cs.advprog.mysawit.dto;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestDetailResponse {

    private UUID id;
    private Double kilogram;
    private String reportNote;
    private HarvestStatus status;
    private List<String> photos;

}