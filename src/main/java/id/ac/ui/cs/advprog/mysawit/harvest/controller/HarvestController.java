package id.ac.ui.cs.advprog.mysawit.harvest.controller;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.ApproveRejectRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/harvest")
@RequiredArgsConstructor
public class HarvestController {

    private final HarvestService harvestService;
    private final HarvestServiceImpl harvestServiceImpl;

    // ── Helper: ambil data user dari request attribute (diset oleh JwtAuthFilter)
    private UUID getUserId(HttpServletRequest request) {
        return (UUID) request.getAttribute("userId");
    }

    private String getRole(HttpServletRequest request) {
        return (String) request.getAttribute("role");
    }

    private UUID getMandorId(HttpServletRequest request) {
        return (UUID) request.getAttribute("mandorId");
    }

    private ResponseEntity<?> forbiddenRole(String expected, String actual) {
        return ResponseEntity.status(403)
                .body(java.util.Map.of(
                        "error", "Akses ditolak. Role dibutuhkan: " + expected + ", role kamu: " + actual
                ));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BURUH: Submit panen (JSON)
    // POST /api/harvest
    // Header: Authorization: Bearer <token>
    // Body: { "kilogram": 10.5, "reportNote": "..." }
    // ══════════════════════════════════════════════════════════════════════════
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitJson(
            HttpServletRequest request,
            @Valid @RequestBody HarvestRequest body
    ) {
        String role = getRole(request);
        if (!"BURUH".equals(role)) {
            return forbiddenRole("BURUH", role);
        }

        UUID buruhId  = getUserId(request);
        UUID mandorId = getMandorId(request); // dari JWT payload Buruh

        HarvestResponse response = harvestService.submitHarvest(body, buruhId, mandorId);
        return ResponseEntity.status(201).body(response);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BURUH: Submit panen + upload foto (multipart)
    // POST /api/harvest
    // Header: Authorization: Bearer <token>
    // Form-data: kilogram, reportNote, photos (multiple files)
    // ══════════════════════════════════════════════════════════════════════════
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitMultipart(
            HttpServletRequest request,
            @Valid @ModelAttribute HarvestRequest body,
            @RequestPart(required = false) List<MultipartFile> photos
    ) {
        String role = getRole(request);
        if (!"BURUH".equals(role)) {
            return forbiddenRole("BURUH", role);
        }

        UUID buruhId  = getUserId(request);
        UUID mandorId = getMandorId(request);

        HarvestResponse response = harvestService.submitHarvest(body, buruhId, mandorId);

        if (photos != null && !photos.isEmpty()) {
            harvestServiceImpl.savePhotos(response.getId(), photos);
        }

        return ResponseEntity.status(201).body(response);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BURUH: Lihat riwayat panen sendiri + alasan tolak kalau REJECTED
    // GET /api/harvest/my?startDate=&endDate=&status=
    // ══════════════════════════════════════════════════════════════════════════
    @GetMapping("/my")
    public ResponseEntity<?> myHarvest(
            HttpServletRequest request,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) HarvestStatus status
    ) {
        String role = getRole(request);
        if (!"BURUH".equals(role)) {
            return forbiddenRole("BURUH", role);
        }

        UUID buruhId = getUserId(request);

        return ResponseEntity.ok(
                harvestService.getMyHarvest(buruhId, startDate, endDate, status)
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MANDOR: Lihat panen semua bawahan
    // GET /api/harvest/bawahan?buruhId=&tanggalPanen=
    // ══════════════════════════════════════════════════════════════════════════
    @GetMapping("/bawahan")
    public ResponseEntity<?> panenBawahan(
            HttpServletRequest request,

            @RequestParam(required = false) UUID buruhId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalPanen
    ) {
        String role = getRole(request);
        if (!"MANDOR".equals(role)) {
            return forbiddenRole("MANDOR", role);
        }

        UUID mandorId = getUserId(request); // Mandor = subject token

        return ResponseEntity.ok(
                harvestService.getPanenBawahan(mandorId, buruhId, tanggalPanen)
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MANDOR: Approve panen
    // PATCH /api/harvest/{id}/approve
    // ══════════════════════════════════════════════════════════════════════════
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approvePanen(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        String role = getRole(request);
        if (!"MANDOR".equals(role)) {
            return forbiddenRole("MANDOR", role);
        }

        UUID mandorId = getUserId(request);

        return ResponseEntity.ok(harvestService.approvePanen(id, mandorId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MANDOR: Reject panen (alasan wajib diisi)
    // PATCH /api/harvest/{id}/reject
    // Body: { "rejectionReason": "..." }
    // ══════════════════════════════════════════════════════════════════════════
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectPanen(
            @PathVariable UUID id,
            HttpServletRequest request,
            @RequestBody ApproveRejectRequest body
    ) {
        String role = getRole(request);
        if (!"MANDOR".equals(role)) {
            return forbiddenRole("MANDOR", role);
        }

        UUID mandorId = getUserId(request);

        return ResponseEntity.ok(
                harvestService.rejectPanen(id, mandorId, body.getRejectionReason())
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Detail satu panen (semua role bisa akses)
    // GET /api/harvest/{id}
    // ══════════════════════════════════════════════════════════════════════════
    @GetMapping("/{id}")
    public ResponseEntity<HarvestDetailResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(harvestService.getDetail(id));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Delete (untuk testing — bisa dihapus di production)
    // DELETE /api/harvest/{id}
    // ══════════════════════════════════════════════════════════════════════════
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable UUID id) {
        harvestService.deleteHarvest(id);
        return ResponseEntity.ok().build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Health check
    // GET /api/harvest/health
    // ══════════════════════════════════════════════════════════════════════════
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}