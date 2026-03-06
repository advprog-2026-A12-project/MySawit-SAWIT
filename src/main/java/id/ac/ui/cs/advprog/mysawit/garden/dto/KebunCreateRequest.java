package id.ac.ui.cs.advprog.mysawit.garden.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class KebunCreateRequest {

    @NotBlank(message = "Nama kebun wajib diisi")
    @Size(min = 3, max = 100, message = "Nama kebun harus antara 3-100 karakter")
    private String nama;

    @NotBlank(message = "Kode kebun wajib diisi")
    @Size(max = 50, message = "Kode kebun maksimal 50 karakter")
    private String kode;

    @NotNull(message = "Luas hektare wajib diisi")
    @Positive(message = "Luas hektare harus lebih dari 0")
    private Double luasHektare;

    @NotNull(message = "Koordinat 1 latitude wajib diisi")
    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord1Lat;

    @NotNull(message = "Koordinat 1 longitude wajib diisi")
    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord1Lng;

    @NotNull(message = "Koordinat 2 latitude wajib diisi")
    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord2Lat;

    @NotNull(message = "Koordinat 2 longitude wajib diisi")
    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord2Lng;

    @NotNull(message = "Koordinat 3 latitude wajib diisi")
    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord3Lat;

    @NotNull(message = "Koordinat 3 longitude wajib diisi")
    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord3Lng;

    @NotNull(message = "Koordinat 4 latitude wajib diisi")
    @DecimalMin(value = "-90.0", message = "Latitude harus antara -90 dan 90")
    @DecimalMax(value = "90.0", message = "Latitude harus antara -90 dan 90")
    private Double coord4Lat;

    @NotNull(message = "Koordinat 4 longitude wajib diisi")
    @DecimalMin(value = "-180.0", message = "Longitude harus antara -180 dan 180")
    @DecimalMax(value = "180.0", message = "Longitude harus antara -180 dan 180")
    private Double coord4Lng;
}
