package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public interface DeliveryState {

    void advanceStatus(Delivery delivery);

    void mandorApprove(Delivery delivery, boolean isApproved, String rejectionReason);

    void adminApprove(Delivery delivery, boolean isApproved, Double approvedPayloadKg, String rejectionReason);

    String getStatusName();
}
