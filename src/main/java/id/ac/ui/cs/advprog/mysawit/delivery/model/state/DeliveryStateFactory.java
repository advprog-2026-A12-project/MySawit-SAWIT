package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

public class DeliveryStateFactory {

    private DeliveryStateFactory() {

    }

    public static DeliveryState getState(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status pengiriman tidak boleh null");
        }
        return switch (status) {
            case "MEMUAT" -> new MuatState();
            case "MENGIRIM" -> new MengirimState();
            case "TIBA_DI_TUJUAN" -> new TibaDiTujuanState();
            default -> throw new IllegalArgumentException("Status pengiriman tidak dikenali: " + status);
        };
    }
}
