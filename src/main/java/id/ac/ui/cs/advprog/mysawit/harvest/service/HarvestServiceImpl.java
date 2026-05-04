package id.ac.ui.cs.advprog.mysawit.harvest.service;

import id.ac.ui.cs.advprog.mysawit.harvest.client.AuthClient;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.mysawit.harvest.model.Harvest;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestPhoto;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.repository.HarvestRepository;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

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
    private final AuthClient authClient;
    private final ApplicationEventPublisher eventPublisher;

    // EXCEPTIONS
    public static class HarvestAlreadySubmittedException extends RuntimeException {
        public HarvestAlreadySubmittedException(String message) {
            super(message);
        }
    }

    public static class HarvestNotFoundException extends RuntimeException {
        public HarvestNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedMandorException extends RuntimeException {
        public UnauthorizedMandorException(String message) {
            super(message);
        }
    }

    public static class InvalidStatusTransitionException extends RuntimeException {
        public InvalidStatusTransitionException(String message) {
            super(message);
        }
    }

    @Override
    @Transactional
    public HarvestResponse submitHarvest(HarvestRequest request, UUID buruhId, UUID mandorId) {
        if (mandorId == null) {
            throw new RuntimeException("Buruh belum punya mandor");
        }

        LocalDate today = LocalDate.now();

        if (harvestRepository.existsByBuruhIdAndHarvestDate(buruhId, today)) {
            throw new HarvestAlreadySubmittedException("Sudah submit panen hari ini");
        }

        Harvest harvest = Harvest.builder()
                .buruhId(buruhId)
                .mandorId(mandorId)
                .harvestDate(today)
                .kilogram(request.getKilogram())
                .reportNote(request.getReportNote())
                .status(HarvestStatus.PENDING)
                .bisaDiangkutTruk(false)
                .photos(new ArrayList<>())
                .build();

        return mapToResponse(harvestRepository.save(harvest));
    }

    @Override
    public List<HarvestResponse> getMyHarvest(
            UUID buruhId, LocalDate startDate, LocalDate endDate, HarvestStatus status) {
        return harvestRepository.findWithFilter(buruhId, startDate, endDate, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HarvestResponse> getPanenBawahan(UUID mandorId, UUID buruhId, LocalDate tanggalPanen) {
        return harvestRepository.findByMandorWithFilter(mandorId, buruhId, tanggalPanen)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public HarvestResponse approvePanen(UUID harvestId, UUID mandorId) {
        Harvest harvest = findAndValidateOwnership(harvestId, mandorId);

        if (harvest.getStatus() != HarvestStatus.PENDING) {
            throw new InvalidStatusTransitionException("Status tidak valid");
        }

        harvest.setStatus(HarvestStatus.APPROVED);
        harvest.setBisaDiangkutTruk(true);
        harvest.setActionedByMandorId(mandorId);
        harvest.setRejectionReason(null);

        Harvest savedHarvest = harvestRepository.save(harvest);

        eventPublisher.publishEvent(new HarvestApprovedEvent(
                savedHarvest.getId(),
                savedHarvest.getBuruhId(),
                savedHarvest.getMandorId(),
                savedHarvest.getKilogram()
        ));

        return mapToResponse(savedHarvest);
    }

    @Override
    @Transactional
    public HarvestResponse rejectPanen(UUID harvestId, UUID mandorId, String alasan) {
        if (alasan == null || alasan.isBlank()) {
            throw new IllegalArgumentException("Alasan wajib diisi");
        }

        Harvest harvest = findAndValidateOwnership(harvestId, mandorId);

        if (harvest.getStatus() != HarvestStatus.PENDING) {
            throw new InvalidStatusTransitionException("Status tidak valid");
        }

        harvest.setStatus(HarvestStatus.REJECTED);
        harvest.setBisaDiangkutTruk(false);
        harvest.setRejectionReason(alasan);
        harvest.setActionedByMandorId(mandorId);

        return mapToResponse(harvestRepository.save(harvest));
    }

    @Override
    public HarvestDetailResponse getDetail(UUID harvestId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Not found"));
        return mapToDetail(harvest);
    }

    @Override
    public void deleteHarvest(UUID harvestId) {
        harvestRepository.deleteById(harvestId);
    }

    // =========================
    // LOGIKA UPLOAD MULTIPLE PHOTOS
    // =========================
    @Override
    @Transactional
    public void savePhotos(UUID harvestId, List<MultipartFile> photos) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Not found"));

        for (MultipartFile file : photos) {
            if (file.isEmpty()) {
                continue; // Menambahkan kurung kurawal agar lolos Checkstyle
            }

            try {
                // Upload ke Cloudinary
                Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String url = upload.get("secure_url").toString();

                // Buat Entitas Foto baru
                HarvestPhoto photo = HarvestPhoto.builder()
                        .fileUrl(url)
                        .harvest(harvest)
                        .build();

                // Masukkan ke List di objek Harvest
                harvest.getPhotos().add(photo);

            } catch (IOException e) {
                throw new RuntimeException("Upload ke Cloudinary gagal", e);
            }
        }
        harvestRepository.save(harvest);
    }

    private Harvest findAndValidateOwnership(UUID harvestId, UUID mandorId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Not found"));

        if (!harvest.getMandorId().equals(mandorId)) {
            throw new UnauthorizedMandorException("Unauthorized");
        }
        return harvest;
    }

    private HarvestResponse mapToResponse(Harvest harvest) {
        return HarvestResponse.builder()
                .id(harvest.getId())
                .buruhId(harvest.getBuruhId())
                .mandorId(harvest.getMandorId())
                .harvestDate(harvest.getHarvestDate())
                .kilogram(harvest.getKilogram())
                .status(harvest.getStatus())
                .rejectionReason(harvest.getRejectionReason())
                .bisaDiangkutTruk(harvest.getBisaDiangkutTruk())
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
                .buruhId(harvest.getBuruhId())
                .mandorId(harvest.getMandorId())
                .harvestDate(harvest.getHarvestDate())
                .kilogram(harvest.getKilogram())
                .reportNote(harvest.getReportNote())
                .status(harvest.getStatus())
                .rejectionReason(harvest.getRejectionReason())
                .bisaDiangkutTruk(harvest.getBisaDiangkutTruk())
                .photos(photoUrls)
                .build();
    }
}