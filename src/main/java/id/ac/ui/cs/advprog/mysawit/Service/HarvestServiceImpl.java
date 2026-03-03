package id.ac.ui.cs.advprog.mysawit.service;

import id.ac.ui.cs.advprog.mysawit.Model.*;
import id.ac.ui.cs.advprog.mysawit.Repository.HarvestRepository;
import id.ac.ui.cs.advprog.mysawit.Service.HarvestService;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;

    @Override
    public HarvestResponse submitHarvest(
            HarvestRequest request,
            UUID buruhId,
            String role
    ) {

        //  Validasi role
        if (!"BURUH".equals(role)) {
            throw new IllegalStateException("Only BURUH can submit harvest");
        }

        LocalDate today = LocalDate.now();

        // Validasi submit sekali per hari
        if (harvestRepository.existsByBuruhIdAndHarvestDate(buruhId, today)) {
            throw new IllegalStateException("Sudah submit panen hari ini");
        }

        Harvest harvest = Harvest.builder()
                .buruhId(buruhId)
                .harvestDate(today)
                .kilogram(request.getKilogram())
                .reportNote(request.getReportNote())
                .status(HarvestStatus.PENDING)
                .build();

        Harvest saved = harvestRepository.save(harvest);

        return mapToResponse(saved);
    }

    @Override
    public List<HarvestResponse> getMyHarvest(
            UUID buruhId,
            LocalDate startDate,
            LocalDate endDate,
            HarvestStatus status
    ) {

        List<Harvest> harvests =
                harvestRepository.findWithFilter(buruhId, startDate, endDate, status);

        return harvests.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HarvestDetailResponse getDetail(UUID harvestId) {

        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new RuntimeException("Harvest not found"));

        return mapToDetail(harvest);
    }

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
                : harvest.getPhotos()
                .stream()
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