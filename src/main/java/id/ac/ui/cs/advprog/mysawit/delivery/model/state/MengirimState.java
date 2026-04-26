package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class MengirimState implements DeliveryState {

    private static final String STATUS_TIBA = "TIBA_DI_TUJUAN";

    @Override
    public void advanceStatus(Delivery delivery) {
        delivery.setStatus(STATUS_TIBA);
        delivery.setArrivedAt(java.time.LocalDateTime.now());
    }

    @Override
    public void mandorApprove(Delivery delivery, boolean isApproved, String rejectionReason) {
        throw new IllegalStateException(
                "Persetujuan Mandor tidak dapat dilakukan pada status: " + getStatusName());
    }

    @Override
    public void adminApprove(Delivery delivery, boolean isApproved,
            Double approvedPayloadKg, String rejectionReason) {
        throw new IllegalStateException(
                "Persetujuan Admin tidak dapat dilakukan pada status: " + getStatusName());
    }

    @Override
    public String getStatusName() {
        return "MENGIRIM";
    }
}
