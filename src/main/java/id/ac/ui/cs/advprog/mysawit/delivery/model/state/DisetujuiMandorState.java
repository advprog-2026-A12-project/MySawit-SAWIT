package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class DisetujuiMandorState implements DeliveryState {

    private static final String STATUS_SELESAI = "SELESAI";
    private static final String STATUS_DITOLAK_ADMIN = "DITOLAK_ADMIN";

    @Override
    public void advanceStatus(Delivery delivery) {
        throw new IllegalStateException(
                "Pengiriman menunggu persetujuan Admin. Tidak dapat memajukan status secara manual.");
    }

    @Override
    public void mandorApprove(Delivery delivery, boolean isApproved, String rejectionReason) {
        throw new IllegalStateException(
                "Pengiriman sudah disetujui Mandor. Tidak dapat melakukan persetujuan Mandor lagi.");
    }

    @Override
    public void adminApprove(Delivery delivery, boolean isApproved,
            Double approvedPayloadKg, String rejectionReason) {
        if (isApproved) {
            delivery.setStatus(STATUS_SELESAI);
            delivery.setApprovedPayloadKg(approvedPayloadKg != null
                    ? approvedPayloadKg
                    : delivery.getPayloadKg());
            delivery.setRejectionReason(null);
        } else {
            delivery.setStatus(STATUS_DITOLAK_ADMIN);
            delivery.setRejectionReason(rejectionReason);
        }
    }

    @Override
    public String getStatusName() {
        return "DISETUJUI_MANDOR";
    }
}
