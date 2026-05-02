package id.ac.ui.cs.advprog.mysawit.harvest.controller;

import id.ac.ui.cs.advprog.mysawit.harvest.client.AuthClient;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.*;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/harvest")
@RequiredArgsConstructor
public class HarvestController {

    private final HarvestService harvestService;
    private final HarvestServiceImpl harvestServiceImpl;
    private final AuthClient authClient;

    // =========================
    // HELPERS
    // =========================
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    private ResponseEntity<?> forbiddenRole(String expected, String actual) {
        return ResponseEntity.status(403).body(
                Map.of("error",
                        "Akses ditolak. Role dibutuhkan: " + expected + ", role kamu: " + actual)
        );
    }

    // =========================
    // BURUH: SUBMIT JSON
    // =========================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitJson(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody HarvestRequest body
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"BURUH".equals(user.getRole())) {
            return forbiddenRole("BURUH", user.getRole());
        }

        UUID buruhId = user.getId();
        UUID mandorId = user.getMandorId();

        HarvestResponse response = harvestService.submitHarvest(body, buruhId, mandorId);

        return ResponseEntity.status(201).body(response);
    }

    // =========================
    // BURUH: SUBMIT MULTIPART + FOTO
    // =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitMultipart(
            @RequestHeader("Authorization") String authHeader,
            @Valid @ModelAttribute HarvestRequest body,
            @RequestPart(required = false) List<MultipartFile> photos
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"BURUH".equals(user.getRole())) {
            return forbiddenRole("BURUH", user.getRole());
        }

        UUID buruhId = user.getId();
        UUID mandorId = user.getMandorId();

        HarvestResponse response = harvestService.submitHarvest(body, buruhId, mandorId);

        if (photos != null && !photos.isEmpty()) {
            harvestServiceImpl.savePhotos(response.getId(), photos);
        }

        return ResponseEntity.status(201).body(response);
    }

    // =========================
    // BURUH: MY HARVEST
    // =========================
    @GetMapping("/my")
    public ResponseEntity<?> myHarvest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) HarvestStatus status
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"BURUH".equals(user.getRole())) {
            return forbiddenRole("BURUH", user.getRole());
        }

        return ResponseEntity.ok(
                harvestService.getMyHarvest(user.getId(), startDate, endDate, status)
        );
    }

    // =========================
    // MANDOR: BAWAHAN
    // =========================
    @GetMapping("/bawahan")
    public ResponseEntity<?> panenBawahan(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) UUID buruhId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalPanen
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"MANDOR".equals(user.getRole())) {
            return forbiddenRole("MANDOR", user.getRole());
        }

        UUID mandorId = user.getId();

        return ResponseEntity.ok(
                harvestService.getPanenBawahan(mandorId, buruhId, tanggalPanen)
        );
    }

    // =========================
    // MANDOR: APPROVE
    // =========================
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approvePanen(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"MANDOR".equals(user.getRole())) {
            return forbiddenRole("MANDOR", user.getRole());
        }

        return ResponseEntity.ok(
                harvestService.approvePanen(id, user.getId())
        );
    }

    // =========================
    // MANDOR: REJECT
    // =========================
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectPanen(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody ApproveRejectRequest body
    ) {
        String token = extractToken(authHeader);

        UserProfile user = authClient.getMe(token);

        if (!"MANDOR".equals(user.getRole())) {
            return forbiddenRole("MANDOR", user.getRole());
        }

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