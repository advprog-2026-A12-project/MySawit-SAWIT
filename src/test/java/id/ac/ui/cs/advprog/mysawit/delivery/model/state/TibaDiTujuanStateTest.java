package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TibaDiTujuanStateTest {

    private TibaDiTujuanState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new TibaDiTujuanState();
        delivery = Delivery.builder()
                .status("TIBA_DI_TUJUAN")
                .payloadKg(200.0)
                .build();
    }

    @Test
    void getStatusNameShouldReturnTibaDiTujuan() {
        assertThat(state.getStatusName()).isEqualTo("TIBA_DI_TUJUAN");
    }

    @Test
    void advanceStatusShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.advanceStatus(delivery))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tiba di tujuan");
    }

}
