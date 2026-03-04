package id.ac.ui.cs.advprog.mysawit.garden.controller;

import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import id.ac.ui.cs.advprog.mysawit.garden.service.KebunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kebun")
public class KebunController {

    private final KebunService kebunService;

    public KebunController(KebunService kebunService) {
        this.kebunService = kebunService;
    }

    @PostMapping
    public ResponseEntity<Kebun> createKebun(@RequestBody Kebun kebun) {
        return ResponseEntity.ok(kebunService.createKebun(kebun));
    }

    @GetMapping
    public ResponseEntity<List<Kebun>> getAllKebun() {
        return ResponseEntity.ok(kebunService.getAllKebun());
    }
}