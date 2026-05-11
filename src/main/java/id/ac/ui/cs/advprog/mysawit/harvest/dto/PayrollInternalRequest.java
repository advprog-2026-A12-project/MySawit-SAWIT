package id.ac.ui.cs.advprog.mysawit.harvest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PayrollInternalRequest {
    private UUID userId;
    private String userRole;
    private String referenceType;
    private UUID referenceId;
    private Double kilogram;
}