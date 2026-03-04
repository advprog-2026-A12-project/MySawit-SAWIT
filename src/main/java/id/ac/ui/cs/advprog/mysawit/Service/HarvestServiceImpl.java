package id.ac.ui.cs.advprog.mysawit.Service;

import id.ac.ui.cs.advprog.mysawit.Model.Harvest;
import id.ac.ui.cs.advprog.mysawit.Model.HarvestPhoto;
import id.ac.ui.cs.advprog.mysawit.Model.HarvestStatus;

import id.ac.ui.cs.advprog.mysawit.Repository.HarvestRepository;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.dto.HarvestResponse;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;
    private final Cloudinary cloudinary;

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
        if (harvestRepository.existsByBuruhIdAndHarvestDate(buruhId, today)) {
            throw new HarvestAlreadySubmittedException("Sudah submit panen hari ini");
        }

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
    public List<HarvestResponse> getMyHarvest(
            UUID buruhId,
            LocalDate startDate,
            LocalDate endDate,
            HarvestStatus status
    ) {
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
                // Upload file langsung ke Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

                // Ambil link URL publik (https) dari hasil upload
                String imageUrl = uploadResult.get("secure_url").toString();

                HarvestPhoto photo = HarvestPhoto.builder()
                        .fileUrl(imageUrl) // Simpan link Cloudinary ke database
                        .harvest(harvest)
                        .build();

                harvest.getPhotos().add(photo);

            } catch (IOException e) {
                throw new RuntimeException("Gagal upload foto ke Cloudinary", e);
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

    // Delete Harvest (Untuk testing)
    @Override
    public void deleteHarvest(UUID harvestId) {
        // Cek apakah datanya ada
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new RuntimeException("Harvest not found"));

        // Hapus dari database
        harvestRepository.delete(harvest);
    }
}