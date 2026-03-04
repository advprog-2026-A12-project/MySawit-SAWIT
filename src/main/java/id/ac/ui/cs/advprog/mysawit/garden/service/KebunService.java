package id.ac.ui.cs.advprog.mysawit.garden.service;

import id.ac.ui.cs.advprog.mysawit.garden.model.Kebun;
import id.ac.ui.cs.advprog.mysawit.garden.repository.KebunRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KebunService {

    private final KebunRepository kebunRepository;

    public KebunService(KebunRepository kebunRepository) {
        this.kebunRepository = kebunRepository;
    }

    public Kebun createKebun(Kebun kebun) {

        if (kebunRepository.existsByKodeUnik(kebun.getKodeUnik())) {
            throw new RuntimeException("Kode kebun sudah digunakan!");
        }

        if (kebun.getLuas() <= 0) {
            throw new RuntimeException("Luas harus lebih dari 0!");
        }

        return kebunRepository.save(kebun);
    }

    public List<Kebun> getAllKebun() {
        return kebunRepository.findAll();
    }
}