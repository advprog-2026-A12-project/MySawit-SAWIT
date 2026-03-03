package id.ac.ui.cs.advprog.mysawit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestRequest {

    @NotNull(message = "Kilogram wajib diisi")
    @Positive(message = "Kilogram harus lebih dari 0")
    private Double kilogram;

    private String reportNote;
}