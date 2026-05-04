package id.ac.ui.cs.advprog.mysawit.harvest.controller;

import id.ac.ui.cs.advprog.mysawit.harvest.client.AuthClient;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.*;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/harvest")
@RequiredArgsConstructor
public class HarvestController {

    private final HarvestService harvestService;
    private final AuthClient authClient;

    // =========================
    // BURUH: SUBMIT JSON (Tanpa Foto)
    // =========================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('BURUH')")
    public ResponseEntity<?> submitJson(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody HarvestRequest body
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        HarvestResponse response = harvestService.submitHarvest(body, user.getId(), user.getMandorId());
        return ResponseEntity.status(201).body(response);
    }

    // =========================
    // BURUH: SUBMIT MULTIPART + FOTO
    // =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('BURUH')")
    public ResponseEntity<?> submitMultipart(
            @RequestHeader("Authorization") String authHeader,
            @Valid @ModelAttribute HarvestRequest body,
            // UBAH JADI RequestParam AGAR COCOK DENGAN NEXT.JS FormData
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        // 1. Save data laporan
        HarvestResponse response = harvestService.submitHarvest(body, user.getId(), user.getMandorId());

        // 2. Jika ada foto, upload dan simpan
        if (photos != null && !photos.isEmpty()) {
            harvestService.savePhotos(response.getId(), photos);
        }

        return ResponseEntity.status(201).body(response);
    }

    // =========================
    // BURUH: MY HARVEST
    // =========================
    @GetMapping("/my")
    @PreAuthorize("hasRole('BURUH')")
    public ResponseEntity<?> myHarvest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) HarvestStatus status
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        return ResponseEntity.ok(
                harvestService.getMyHarvest(user.getId(), startDate, endDate, status)
        );
    }

    // =========================
    // MANDOR: BAWAHAN
    // =========================
    @GetMapping("/bawahan")
    @PreAuthorize("hasRole('MANDOR')")
    public ResponseEntity<?> panenBawahan(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) UUID buruhId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalPanen
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        return ResponseEntity.ok(
                harvestService.getPanenBawahan(user.getId(), buruhId, tanggalPanen)
        );
    }

    // =========================
    // MANDOR: APPROVE
    // =========================
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANDOR')")
    public ResponseEntity<?> approvePanen(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        return ResponseEntity.ok(
                harvestService.approvePanen(id, user.getId())
        );
    }

    // =========================
    // MANDOR: REJECT
    // =========================
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANDOR')")
    public ResponseEntity<?> rejectPanen(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody ApproveRejectRequest body
    ) {
        String token = authHeader.substring(7);
        UserProfile user = authClient.getMe(token);

        return ResponseEntity.ok(
                harvestService.rejectPanen(
                        id,
                        user.getId(),
                        body.getRejectionReason()
                )
        );
    }

    // =========================
    // DETAIL
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<HarvestDetailResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(
                harvestService.getDetail(id)
        );
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable UUID id) {
        harvestService.deleteHarvest(id);
        return ResponseEntity.ok().build();
    }

    // =========================
    // HEALTH
    // =========================
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}