package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class TibaDiTujuanState implements DeliveryState {

    private static final String STATUS_DISETUJUI_MANDOR = "DISETUJUI_MANDOR";
    private static final String STATUS_DITOLAK_MANDOR = "DITOLAK_MANDOR";

    @Override
    public void advanceStatus(Delivery delivery) {
        throw new IllegalStateException(
                "Pengiriman sudah tiba di tujuan. Menunggu persetujuan Mandor.");
    }

    @Override
    public void mandorApprove(Delivery delivery, boolean isApproved, String rejectionReason) {
        if (isApproved) {
            delivery.setStatus(STATUS_DISETUJUI_MANDOR);
            delivery.setRejectionReason(null);
        } else {
            delivery.setStatus(STATUS_DITOLAK_MANDOR);
            delivery.setRejectionReason(rejectionReason);
        }
    }

    @Override
    public void adminApprove(Delivery delivery, boolean isApproved,
            Double approvedPayloadKg, String rejectionReason) {
        throw new IllegalStateException(
                "Persetujuan Admin tidak dapat dilakukan pada status: " + getStatusName()
                        + ". Diperlukan persetujuan Mandor terlebih dahulu.");
    }

    @Override
    public String getStatusName() {
        return "TIBA_DI_TUJUAN";
    }
}
