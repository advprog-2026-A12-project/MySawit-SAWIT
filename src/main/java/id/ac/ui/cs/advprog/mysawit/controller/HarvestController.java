package id.ac.ui.cs.advprog.mysawit.Controller;

import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.Service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.Service.HarvestServiceImpl;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/harvest")
@RequiredArgsConstructor
public class HarvestController {

    private final HarvestService harvestService;
    private final HarvestServiceImpl harvestServiceImpl;


    // CREATE HARVEST
    //  support JSON
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HarvestResponse> submitJson(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestHeader("X-ROLE") String role,
            @Valid @RequestBody HarvestRequest request
    ) {
        HarvestResponse response = harvestService.submitHarvest(request, userId, role);
        return ResponseEntity.status(201).body(response);
    }


    // CREATE HARVEST + UPLOAD FOTO (Multipart)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HarvestResponse> submitMultipart(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestHeader("X-ROLE") String role,
            @Valid @ModelAttribute HarvestRequest request,
            @RequestPart(required = false) List<MultipartFile> photos
    ) {
        // Simpan harvest
        HarvestResponse response = harvestService.submitHarvest(request, userId, role);

        // Simpan foto jika ada
        if (photos != null && !photos.isEmpty()) {
            harvestServiceImpl.savePhotos(response.getId(), photos);
        }

        return ResponseEntity.status(201).body(response);
    }

    // GET MY HARVEST
    @GetMapping("/my")
    public ResponseEntity<List<HarvestResponse>> myHarvest(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            HarvestStatus status
    ) {
        List<HarvestResponse> list = harvestService.getMyHarvest(userId, startDate, endDate, status);
        return ResponseEntity.ok(list);
    }

    // GET HARVEST DETAIL
    @GetMapping("/{id}")
    public ResponseEntity<HarvestDetailResponse> detail(
            @PathVariable UUID id
    ) {
        HarvestDetailResponse detail = harvestService.getDetail(id);
        return ResponseEntity.ok(detail);
    }

    // DELETE HARVEST untuk testing
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvest(
            @PathVariable UUID id
    ) {
        harvestService.deleteHarvest(id);
        return ResponseEntity.ok().build(); // Mengembalikan status 200 OK
    }
}