package id.ac.ui.cs.advprog.mysawit.Service;

import id.ac.ui.cs.advprog.mysawit.Model.*;
import id.ac.ui.cs.advprog.mysawit.Repository.HarvestRepository;
import id.ac.ui.cs.advprog.mysawit.Service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;

    // Custom Exception
    public static class HarvestAlreadySubmittedException extends RuntimeException {
        public HarvestAlreadySubmittedException(String message) {
            super(message);
        }
    }

    // Submit Harvest
    @Override
    public HarvestResponse submitHarvest(HarvestRequest request, UUID buruhId, String role) {

        // Validasi role
        if (!"BURUH".equals(role)) {
            throw new IllegalStateException("Only BURUH can submit harvest");
        }

        LocalDate today = LocalDate.now();
//        // Validasi submit sekali per hari
//        if (harvestRepository.existsByBuruhIdAndHarvestDate(buruhId, today)) {
//            throw new HarvestAlreadySubmittedException("Sudah submit panen hari ini");
//        }

        Harvest harvest = Harvest.builder()
                .buruhId(buruhId)
                .harvestDate(today)
                .kilogram(request.getKilogram())
                .reportNote(request.getReportNote())
                .status(HarvestStatus.PENDING)
                .photos(new ArrayList<>())
                .build();

        Harvest saved = harvestRepository.save(harvest);

        return mapToResponse(saved);
    }

    // Get My Harvest
    @Override
    public List<HarvestResponse> getMyHarvest(UUID buruhId, LocalDate startDate, LocalDate endDate, HarvestStatus status) {
        List<Harvest> harvests = harvestRepository.findWithFilter(buruhId, startDate, endDate, status);
        return harvests.stream().map(this::mapToResponse).toList();
    }

    // Get Harvest Detail
    @Override
    public HarvestDetailResponse getDetail(UUID harvestId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new RuntimeException("Harvest not found"));

        return mapToDetail(harvest);
    }

    // Save Photos
    public void savePhotos(UUID harvestId, List<MultipartFile> photos) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new RuntimeException("Harvest not found"));

        if (harvest.getPhotos() == null) {
            harvest.setPhotos(new ArrayList<>());
        }

        for (MultipartFile file : photos) {
            try {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Paths.get("uploads/" + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                HarvestPhoto photo = HarvestPhoto.builder()
                        .fileUrl("/uploads/" + filename)
                        .harvest(harvest)
                        .build();

                harvest.getPhotos().add(photo);

            } catch (IOException e) {
                throw new RuntimeException("Failed to save photo", e);
            }
        }

        harvestRepository.save(harvest);
    }

    // Mapper
    private HarvestResponse mapToResponse(Harvest harvest) {
        return HarvestResponse.builder()
                .id(harvest.getId())
                .harvestDate(harvest.getHarvestDate())
                .kilogram(harvest.getKilogram())
                .status(harvest.getStatus())
                .build();
    }

    private HarvestDetailResponse mapToDetail(Harvest harvest) {
        List<String> photoUrls = harvest.getPhotos() == null
                ? List.of()
                : harvest.getPhotos().stream()
                .map(HarvestPhoto::getFileUrl)
                .toList();

        return HarvestDetailResponse.builder()
                .id(harvest.getId())
                .kilogram(harvest.getKilogram())
                .reportNote(harvest.getReportNote())
                .status(harvest.getStatus())
                .photos(photoUrls)
                .build();
    }
}