package id.ac.ui.cs.advprog.mysawit.garden.controller;

import id.ac.ui.cs.advprog.mysawit.garden.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunSupirAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.service.KebunService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller untuk manajemen kebun sawit.
 * Autentikasi dan otorisasi ditangani oleh {@link id.ac.ui.cs.advprog.mysawit.garden.security.GardenJwtFilter}.
 */
@RestController
@RequestMapping("/api/kebun")
public class KebunController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final KebunService kebunService;

    public KebunController(KebunService kebunService) {
        this.kebunService = kebunService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KebunDetailResponse>> createKebun(
            @Valid @RequestBody KebunCreateRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunDetailResponse response = kebunService.createKebun(request, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kebun berhasil dibuat", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KebunResponse>>> getAllKebun(
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String kode,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        List<KebunResponse> responses = kebunService.getAllKebun(nama, kode, token);
        return ResponseEntity.ok(
                ApiResponse.success("Daftar kebun berhasil diambil", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KebunDetailResponse>> getKebunById(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunDetailResponse response = kebunService.getKebunById(id, token);
        return ResponseEntity.ok(
                ApiResponse.success("Detail kebun berhasil diambil", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KebunDetailResponse>> updateKebun(
            @PathVariable UUID id,
            @Valid @RequestBody KebunUpdateRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunDetailResponse response = kebunService.updateKebun(id, request, token);
        return ResponseEntity.ok(
                ApiResponse.success("Kebun berhasil diperbarui", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKebun(@PathVariable UUID id) {
        kebunService.deleteKebun(id);
        return ResponseEntity.ok(
                ApiResponse.success("Kebun berhasil dihapus", null));
    }

    @PostMapping("/{id}/assign-mandor")
    public ResponseEntity<ApiResponse<KebunDetailResponse>> assignMandor(
            @PathVariable UUID id,
            @Valid @RequestBody AssignMandorRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunDetailResponse response = kebunService.assignMandor(id, request.getMandorId(), token);
        return ResponseEntity.ok(
                ApiResponse.success("Mandor berhasil ditugaskan ke kebun", response));
    }

    @DeleteMapping("/{id}/mandor")
    public ResponseEntity<ApiResponse<KebunDetailResponse>> unassignMandor(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunDetailResponse response = kebunService.unassignMandor(id, token);
        return ResponseEntity.ok(
                ApiResponse.success("Mandor berhasil dicopot dari kebun", response));
    }

    @PostMapping("/{id}/assign-supir")
    public ResponseEntity<ApiResponse<KebunSupirAssignmentResponse>> assignSupir(
            @PathVariable UUID id,
            @Valid @RequestBody AssignSupirRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        KebunSupirAssignmentResponse response = kebunService.assignSupir(id, request.getSupirId(), token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supir berhasil ditugaskan ke kebun", response));
    }

    @DeleteMapping("/{id}/supir/{supirId}")
    public ResponseEntity<ApiResponse<Void>> unassignSupir(
            @PathVariable UUID id,
            @PathVariable UUID supirId) {
        kebunService.unassignSupir(id, supirId);
        return ResponseEntity.ok(
                ApiResponse.success("Supir berhasil dicopot dari kebun", null));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}