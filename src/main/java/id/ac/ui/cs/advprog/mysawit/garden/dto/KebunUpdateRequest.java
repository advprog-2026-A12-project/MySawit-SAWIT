package id.ac.ui.cs.advprog.mysawit.garden.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KebunUpdateRequest {

    @Size(min = 3, max = 100, message = "Nama kebun harus antara 3-100 karakter")
    private String nama;

    @Positive(message = "Luas hektare harus lebih dari 0")
    private Double luasHektare;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord1Lat;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord1Lng;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord2Lat;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord2Lng;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord3Lat;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord3Lng;

    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord4Lat;

    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord4Lng;
}
