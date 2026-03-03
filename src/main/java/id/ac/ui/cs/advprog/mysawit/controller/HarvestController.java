package id.ac.ui.cs.advprog.mysawit.Controller;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.Service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/harvest")
@RequiredArgsConstructor
public class HarvestController {

    private final HarvestService harvestService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submit(
            @RequestHeader("X-USER-ID") UUID buruhId,
            @RequestHeader("X-ROLE") String role,
            @Valid @RequestPart HarvestRequest request,
            @RequestPart(required = false) List<MultipartFile> photos
    ) {

        return ResponseEntity.status(201)
                .body(harvestService.submitHarvest(
                        request,
                        buruhId,
                        role
                ));
    }

    @GetMapping("/my")
    public ResponseEntity<?> myHarvest(
            @RequestHeader("X-USER-ID") UUID buruhId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            HarvestStatus status
    ) {

        return ResponseEntity.ok(
                harvestService.getMyHarvest(
                        buruhId,
                        startDate,
                        endDate,
                        status
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(harvestService.getDetail(id));
    }
}