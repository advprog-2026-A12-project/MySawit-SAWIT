package id.ac.ui.cs.advprog.mysawit.delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateDeliveryRequest {
    private UUID supirId;
    private UUID harvestId;
    private Double payloadKg;
    private String supirName;
}
