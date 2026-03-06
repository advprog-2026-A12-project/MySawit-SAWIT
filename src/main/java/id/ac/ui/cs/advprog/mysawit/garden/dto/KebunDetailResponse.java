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
public class KebunDetailResponse {

    private UUID id;
    private String nama;
    private String kode;
    private Double luasHektare;
    private Double coord1Lat;
    private Double coord1Lng;
    private Double coord2Lat;
    private Double coord2Lng;
    private Double coord3Lat;
    private Double coord3Lng;
    private Double coord4Lat;
    private Double coord4Lng;
    private UUID mandorId;

    @JsonProperty("isActive")
    private Boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}
