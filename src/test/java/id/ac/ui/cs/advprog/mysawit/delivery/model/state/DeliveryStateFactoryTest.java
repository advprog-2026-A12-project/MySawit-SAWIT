package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryStateFactoryTest {

    @Test
    void getStateMuatShouldReturnMuatState() {
        DeliveryState state = DeliveryStateFactory.getState("MEMUAT");
        assertThat(state).isInstanceOf(MuatState.class);
    }

    @Test
    void getStateMengirimShouldReturnMengirimState() {
        DeliveryState state = DeliveryStateFactory.getState("MENGIRIM");
        assertThat(state).isInstanceOf(MengirimState.class);
    }

    @Test
    void getStateTibaDiTujuanShouldReturnTibaDiTujuanState() {
        DeliveryState state = DeliveryStateFactory.getState("TIBA_DI_TUJUAN");
        assertThat(state).isInstanceOf(TibaDiTujuanState.class);
    }

    @Test
    void getStateUnknownShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> DeliveryStateFactory.getState("STATUS_TIDAK_ADA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("STATUS_TIDAK_ADA");
    }

    @Test
    void getStateNullShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> DeliveryStateFactory.getState(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }
}
