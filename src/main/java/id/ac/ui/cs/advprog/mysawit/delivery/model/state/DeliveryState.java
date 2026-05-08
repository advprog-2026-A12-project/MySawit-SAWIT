package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;

public interface DeliveryState {

    void advanceStatus(Delivery delivery);

    String getStatusName();
}
