package id.ac.ui.cs.advprog.mysawit.garden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KebunSupirAssignmentResponse {
    private UUID assignmentId;
    private UUID kebunId;
    private UUID supirId;
    private Boolean isActive;
    private Instant assignedAt;
}
