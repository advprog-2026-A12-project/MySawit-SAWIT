package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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