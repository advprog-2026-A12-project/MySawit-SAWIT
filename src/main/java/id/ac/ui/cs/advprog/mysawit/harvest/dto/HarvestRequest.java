package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class HarvestRequest {

    @Getter
    @NotNull(message = "Kilogram wajib diisi")
    @Min(value = 1, message = "Kilogram minimal 1")
    private Double kilogram;

    @Getter
    @NotBlank(message = "Catatan wajib diisi")
    private String reportNote;

    public void setKilogram(Double kilogram) { this.kilogram = kilogram; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
}