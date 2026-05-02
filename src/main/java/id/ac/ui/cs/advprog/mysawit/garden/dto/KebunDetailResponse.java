package id.ac.ui.cs.advprog.mysawit.garden.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response detail kebun termasuk informasi mandor dan supir.
 * Pada Milestone 75%, field mandor dan supir diperkaya dengan nama/email
 * yang diambil dari Auth Service.
 */
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

    // Mandor detail (enriched dari Auth Service)
    private UUID mandorId;
    private String mandorName;
    private String mandorEmail;

    // Supir detail list (enriched dari Auth Service)
    private List<SupirDetail> supirList;
    private Integer totalSupir;

    @JsonProperty("isActive")
    private Boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Detail supir yang ditugaskan ke kebun ini.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupirDetail {
        private UUID id;
        private String name;
        private String email;
        private Instant assignedAt;
    }
}
