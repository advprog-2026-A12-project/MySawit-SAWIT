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
    public String getStatusName() {
        return "MENGIRIM";
    }
}
