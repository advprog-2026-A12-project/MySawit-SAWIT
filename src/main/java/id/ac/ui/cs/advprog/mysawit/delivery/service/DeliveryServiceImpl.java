package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private static final String STATUS_MEMUAT = "MEMUAT";
    private static final String STATUS_MENGIRIM = "MENGIRIM";
    private static final String STATUS_TIBA = "TIBA_DI_TUJUAN";

    @Override
    @Transactional
    public Delivery createDelivery(CreateDeliveryRequest request, UUID mandorId, String mandorName) {
        if (request.getPayloadKg() == null || request.getPayloadKg() > 400 || request.getPayloadKg() < 1) {
            throw new IllegalArgumentException("Payload harus antara 1 dan 400 Kg");
        }

        Delivery delivery = Delivery.builder()
                .supirId(request.getSupirId())
                .supirName(request.getSupirName())
                .harvestId(request.getHarvestId())
                .mandorId(mandorId)
                .mandorName(mandorName)
                .payloadKg(request.getPayloadKg())
                .status(STATUS_MEMUAT)
                .build();

        return deliveryRepository.save(delivery);
    }

    @Override
    public List<Delivery> getDeliveriesByRole(UUID userId, String role) {
        if ("ADMIN".equals(role)) {
            return deliveryRepository.findAll();
        } else if ("MANDOR".equals(role)) {
            return deliveryRepository.findByMandorId(userId);
        }
        return deliveryRepository.findBySupirId(userId);
    }

    @Override
    public List<Delivery> getDeliveriesBySupirId(UUID supirId) {
        return deliveryRepository.findBySupirId(supirId);
    }

    @Override
    public List<Delivery> getDeliveriesByMandorFiltered(UUID mandorId, String supirName) {
        if (supirName != null && !supirName.isBlank()) {
            return deliveryRepository.findByMandorIdAndSupirNameContainingIgnoreCase(mandorId, supirName);
        }
        return deliveryRepository.findByMandorId(mandorId);
    }

    @Override
    @Transactional
    public Delivery advanceStatus(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery tidak ditemukan"));

        String current = delivery.getStatus();

        if (STATUS_MEMUAT.equals(current)) {
            delivery.setStatus(STATUS_MENGIRIM);
            delivery.setSentAt(LocalDateTime.now());
        } else if (STATUS_MENGIRIM.equals(current)) {
            delivery.setStatus(STATUS_TIBA);
            delivery.setArrivedAt(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Status sudah final: " + current + ". Tidak dapat dilanjutkan.");
        }

        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public Delivery mandorApprove(UUID id, boolean isApproved, String rejectionReason) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery tidak ditemukan"));

        if (isApproved) {
            delivery.setStatus("DISETUJUI_MANDOR");
            delivery.setRejectionReason(null);
        } else {
            delivery.setStatus("DITOLAK_MANDOR");
            delivery.setRejectionReason(rejectionReason);
        }
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public Delivery adminApprove(UUID id, boolean isApproved, Double approvedPayloadKg, String rejectionReason) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery tidak ditemukan"));

        if (isApproved) {
            delivery.setStatus("SELESAI");
            delivery.setApprovedPayloadKg(approvedPayloadKg != null ? approvedPayloadKg : delivery.getPayloadKg());
            delivery.setRejectionReason(null);
        } else {
            delivery.setStatus("DITOLAK_ADMIN");
            delivery.setRejectionReason(rejectionReason);
        }
        return deliveryRepository.save(delivery);
    }
}
