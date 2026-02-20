package id.ac.ui.cs.advprog.mysawit.service;

import id.ac.ui.cs.advprog.mysawit.model.Plantation;
import id.ac.ui.cs.advprog.mysawit.repository.PlantationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlantationService {

    private final PlantationRepository repository;

    public PlantationService(PlantationRepository repository) {
        this.repository = repository;
    }

    public Plantation create(Plantation plantation) {
        return repository.save(plantation);
    }

    public List<Plantation> findAll() {
        return repository.findAll();
    }
}