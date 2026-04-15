package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

public class HarvestRequest {

    // Getter & Setter
    @Getter
    @NotNull(message = "Kilogram wajib diisi")
    @Min(value = 1, message = "Kilogram minimal 1")
    private Double kilogram;

    @Getter
    @NotBlank(message = "Catatan wajib diisi")
    private String reportNote;

    @NotNull(message = "MandorId wajib diisi")
    private UUID mandorId;

    public void setKilogram(Double kilogram) { this.kilogram = kilogram; }
    public UUID getMandorId() { return mandorId; }
    public void setMandorId(UUID mandorId) { this.mandorId = mandorId; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
}