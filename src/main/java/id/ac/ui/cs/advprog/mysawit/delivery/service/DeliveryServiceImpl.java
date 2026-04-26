package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.delivery.model.state.DeliveryState;
import id.ac.ui.cs.advprog.mysawit.delivery.model.state.DeliveryStateFactory;
import id.ac.ui.cs.advprog.mysawit.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private static final String STATUS_MEMUAT = "MEMUAT";

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

        DeliveryState state = DeliveryStateFactory.getState(delivery.getStatus());
        state.advanceStatus(delivery);

        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public Delivery mandorApprove(UUID id, boolean isApproved, String rejectionReason) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery tidak ditemukan"));

        DeliveryState state = DeliveryStateFactory.getState(delivery.getStatus());
        state.mandorApprove(delivery, isApproved, rejectionReason);

        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public Delivery adminApprove(UUID id, boolean isApproved, Double approvedPayloadKg, String rejectionReason) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery tidak ditemukan"));

        DeliveryState state = DeliveryStateFactory.getState(delivery.getStatus());
        state.adminApprove(delivery, isApproved, approvedPayloadKg, rejectionReason);

        return deliveryRepository.save(delivery);
    }
}