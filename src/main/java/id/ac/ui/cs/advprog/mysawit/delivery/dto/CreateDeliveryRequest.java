package id.ac.ui.cs.advprog.mysawit.delivery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

import java.util.List;

@Data
public class CreateDeliveryRequest {
    @NotNull
    private UUID supirId;

    @NotNull
    private List<UUID> harvestIds;

    @NotNull
    @Min(value = 1, message = "Payload minimal 1 Kg")
    @Max(value = 400, message = "Payload maksimal 400 Kg")
    private Double payloadKg;

    private String supirName;
}
