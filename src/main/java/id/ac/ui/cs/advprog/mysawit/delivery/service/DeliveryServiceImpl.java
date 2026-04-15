package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
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

    @Override
    @Transactional
    public Delivery createDelivery(CreateDeliveryRequest request, UUID mandorId, String mandorName) {
        Delivery delivery = Delivery.builder()
                .supirId(request.getSupirId())
                .supirName(request.getSupirName())
                .harvestId(request.getHarvestId())
                .mandorId(mandorId)
                .mandorName(mandorName)
                .payloadKg(request.getPayloadKg())
                .status("MEMUAT")
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
    @Transactional
    public Delivery updateStatus(UUID id, String status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setStatus(status);
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public Delivery mandorApprove(UUID id, boolean isApproved, String rejectionReason) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
                
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
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
                
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
