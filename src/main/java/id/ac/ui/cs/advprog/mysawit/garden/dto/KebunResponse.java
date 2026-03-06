package id.ac.ui.cs.advprog.mysawit.garden.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KebunResponse {

    private UUID id;
    private String nama;
    private String kode;
    private Double luasHektare;
    private UUID mandorId;

    @JsonProperty("isActive")
    private Boolean isActive;

    private Instant createdAt;
}
