package id.ac.ui.cs.advprog.mysawit.garden.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignSupirRequest {

    @NotNull(message = "Supir ID wajib diisi")
    private UUID supirId;
}
