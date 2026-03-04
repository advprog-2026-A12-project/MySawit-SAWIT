package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HarvestRequest {

    @NotNull(message = "Kilogram wajib diisi")
    @Min(value = 1, message = "Kilogram minimal 1")
    private Double kilogram; // ubah dari Integer ke Double

    @NotBlank(message = "Catatan wajib diisi")
    private String reportNote;

    // Getter & Setter
    public Double getKilogram() { return kilogram; }
    public void setKilogram(Double kilogram) { this.kilogram = kilogram; }

    public String getReportNote() { return reportNote; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
}