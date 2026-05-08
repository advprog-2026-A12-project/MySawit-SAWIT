package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    Delivery createDelivery(CreateDeliveryRequest request, UUID mandorId, String mandorName);

    List<Delivery> getDeliveriesByRole(UUID userId, String role);

    List<Delivery> getDeliveriesBySupirId(UUID supirId);

    List<Delivery> getDeliveriesByMandorFiltered(UUID mandorId, String supirName);

    Delivery advanceStatus(UUID id);

    Delivery mandorApprove(UUID id, boolean isApproved, String rejectionReason);

    Delivery adminApprove(UUID id, boolean isApproved, Double approvedPayloadKg, String rejectionReason);

    List<Delivery> getDeliveriesForAdmin(UUID mandorId, String date);
}
