package id.ac.ui.cs.advprog.mysawit.Controller;

import id.ac.ui.cs.advprog.mysawit.Model.Plantation;
import id.ac.ui.cs.advprog.mysawit.Repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.Service.PlantationService;
import org.springframework.web.bind.annotation.*;

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