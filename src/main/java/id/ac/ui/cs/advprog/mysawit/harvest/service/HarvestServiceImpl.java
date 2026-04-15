package id.ac.ui.cs.advprog.mysawit.harvest.service;

import id.ac.ui.cs.advprog.mysawit.harvest.model.Harvest;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestPhoto;
import id.ac.ui.cs.advprog.mysawit.harvest.model.HarvestStatus;
import id.ac.ui.cs.advprog.mysawit.harvest.repository.HarvestRepository;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestDetailResponse;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.dto.HarvestResponse;

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

    // ── Custom Exception ───────────────────────────────────────────────────────

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

    // ── Submit Harvest ─────────────────────────────────────────────────────────

    @Override
    public HarvestResponse submitHarvest(HarvestRequest request, UUID buruhId, UUID mandorId) {
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

    // ── Buruh: Lihat Panen Sendiri ─────────────────────────────────────────────

    @Override
    public List<HarvestResponse> getMyHarvest(UUID buruhId, LocalDate startDate,
                                              LocalDate endDate, HarvestStatus status) {
        return harvestRepository
                .findWithFilter(buruhId, startDate, endDate, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Mandor: Lihat Panen Bawahan ────────────────────────────────────────────

    @Override
    public List<HarvestResponse> getPanenBawahan(UUID mandorId, UUID buruhId, LocalDate tanggalPanen) {
        return harvestRepository
                .findByMandorWithFilter(mandorId, buruhId, tanggalPanen)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Mandor: Approve ────────────────────────────────────────────────────────

    @Override
    public HarvestResponse approvePanen(UUID harvestId, UUID mandorId) {
        Harvest harvest = findAndValidateMandorAkses(harvestId, mandorId);

        if (harvest.getStatus() != HarvestStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Panen sudah berstatus " + harvest.getStatus() + ", tidak bisa diubah"
            );
        }

        harvest.setStatus(HarvestStatus.APPROVED);
        harvest.setBisaDiangkutTruk(true);
        harvest.setActionedByMandorId(mandorId);
        harvest.setRejectionReason(null);

        return mapToResponse(harvestRepository.save(harvest));
    }

    // ── Mandor: Reject ─────────────────────────────────────────────────────────

    @Override
    public HarvestResponse rejectPanen(UUID harvestId, UUID mandorId, String alasan) {
        if (alasan == null || alasan.isBlank()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi");
        }

        Harvest harvest = findAndValidateMandorAkses(harvestId, mandorId);

        if (harvest.getStatus() != HarvestStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Panen sudah berstatus " + harvest.getStatus() + ", tidak bisa diubah"
            );
        }

        harvest.setStatus(HarvestStatus.REJECTED);
        harvest.setBisaDiangkutTruk(false);
        harvest.setRejectionReason(alasan);
        harvest.setActionedByMandorId(mandorId);

        return mapToResponse(harvestRepository.save(harvest));
    }

    // ── Get Detail ─────────────────────────────────────────────────────────────

    @Override
    public HarvestDetailResponse getDetail(UUID harvestId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Harvest not found"));

        return mapToDetail(harvest);
    }

    // ── Save Photos ke Cloudinary ──────────────────────────────────────────────

    public void savePhotos(UUID harvestId, List<MultipartFile> photos) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Harvest not found"));

        if (harvest.getPhotos() == null) {
            harvest.setPhotos(new ArrayList<>());
        }

        for (MultipartFile file : photos) {
            try {
                Map uploadResult = cloudinary.uploader()
                        .upload(file.getBytes(), ObjectUtils.emptyMap());

                String imageUrl = uploadResult.get("secure_url").toString();

                HarvestPhoto photo = HarvestPhoto.builder()
                        .fileUrl(imageUrl)
                        .harvest(harvest)
                        .build();

                harvest.getPhotos().add(photo);

            } catch (IOException e) {
                throw new RuntimeException("Gagal upload foto ke Cloudinary", e);
            }
        }

        harvestRepository.save(harvest);
    }

    // ── Delete (Testing) ───────────────────────────────────────────────────────

    @Override
    public void deleteHarvest(UUID harvestId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Harvest not found"));

        harvestRepository.delete(harvest);
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    private Harvest findAndValidateMandorAkses(UUID harvestId, UUID mandorId) {
        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new HarvestNotFoundException("Harvest not found"));

        if (!harvest.getMandorId().equals(mandorId)) {
            throw new UnauthorizedMandorException("Panen ini bukan bawahan kamu");
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