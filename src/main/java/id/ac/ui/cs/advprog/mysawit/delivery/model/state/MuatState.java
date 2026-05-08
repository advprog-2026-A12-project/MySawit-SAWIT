package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class MuatState implements DeliveryState {

    private static final String STATUS_MENGIRIM = "MENGIRIM";

    @Override
    public void advanceStatus(Delivery delivery) {
        delivery.setStatus(STATUS_MENGIRIM);
        delivery.setSentAt(java.time.LocalDateTime.now());
    }

    @Override
    public String getStatusName() {
        return "MEMUAT";
    }
}
