package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class DitolakAdminState implements DeliveryState {

    @Override
    public void advanceStatus(Delivery delivery) {
        throw new IllegalStateException(
                "Pengiriman telah ditolak oleh Admin. Status sudah final.");
    }

    @Override
    public void mandorApprove(Delivery delivery, boolean isApproved, String rejectionReason) {
        throw new IllegalStateException(
                "Pengiriman telah ditolak oleh Admin. Status sudah final.");
    }

    @Override
    public void adminApprove(Delivery delivery, boolean isApproved,
            Double approvedPayloadKg, String rejectionReason) {
        throw new IllegalStateException(
                "Pengiriman telah ditolak oleh Admin. Status sudah final.");
    }

    @Override
    public String getStatusName() {
        return "DITOLAK_ADMIN";
    }
}
