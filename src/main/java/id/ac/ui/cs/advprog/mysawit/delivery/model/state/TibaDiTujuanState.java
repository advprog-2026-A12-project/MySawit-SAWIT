package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public class TibaDiTujuanState implements DeliveryState {

    @Override
    public void advanceStatus(Delivery delivery) {
        throw new IllegalStateException(
                "Pengiriman sudah tiba di tujuan. Menunggu persetujuan Mandor.");
    }

    @Override
    public String getStatusName() {
        return "TIBA_DI_TUJUAN";
    }
}
