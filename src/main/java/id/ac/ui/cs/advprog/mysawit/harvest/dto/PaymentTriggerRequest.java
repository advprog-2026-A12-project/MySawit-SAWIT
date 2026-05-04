package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentTriggerRequest {
    private UUID harvestId;
    private UUID buruhId;
    private Double totalKilogram;
}