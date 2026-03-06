package id.ac.ui.cs.advprog.mysawit.garden.controller;

import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunCreateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.garden.dto.KebunUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.garden.service.KebunService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kebun")
public class KebunController {

    private final KebunService kebunService;

    public KebunController(KebunService kebunService) {
        this.kebunService = kebunService;
    }

    @PostMapping
    public ResponseEntity<KebunDetailResponse> createKebun(
            @Valid @RequestBody KebunCreateRequest request) {
        KebunDetailResponse response = kebunService.createKebun(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<KebunResponse>> getAllKebun(
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String kode) {
        List<KebunResponse> responses = kebunService.getAllKebun(nama, kode);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KebunDetailResponse> getKebunById(@PathVariable UUID id) {
        KebunDetailResponse response = kebunService.getKebunById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KebunDetailResponse> updateKebun(
            @PathVariable UUID id,
            @Valid @RequestBody KebunUpdateRequest request) {
        KebunDetailResponse response = kebunService.updateKebun(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKebun(@PathVariable UUID id) {
        kebunService.deleteKebun(id);
        return ResponseEntity.ok().build();
    }
}