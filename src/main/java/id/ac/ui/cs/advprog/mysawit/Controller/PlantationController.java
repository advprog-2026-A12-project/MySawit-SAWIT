package id.ac.ui.cs.advprog.mysawit.controller;

import id.ac.ui.cs.advprog.mysawit.model.Plantation;
import id.ac.ui.cs.advprog.mysawit.repository.PlantationRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/plantations")
@CrossOrigin
public class PlantationController {

    private final PlantationRepository repository;

    public PlantationController(PlantationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Plantation> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Plantation create(@RequestBody Plantation plantation) {
        return repository.save(plantation);
    }
}