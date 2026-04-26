package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MengirimStateTest {

    private MengirimState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new MengirimState();
        delivery = Delivery.builder()
                .status("MENGIRIM")
                .payloadKg(200.0)
                .build();
    }

    @Test
    void getStatusNameShouldReturnMengirim() {
        assertThat(state.getStatusName()).isEqualTo("MENGIRIM");
    }

    @Test
    void advanceStatusShouldTransitionToTibaDiTujuan() {
        state.advanceStatus(delivery);
        assertThat(delivery.getStatus()).isEqualTo("TIBA_DI_TUJUAN");
        assertThat(delivery.getArrivedAt()).isNotNull();
    }

    @Test
    void mandorApproveShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.mandorApprove(delivery, true, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MENGIRIM");
    }

    @Test
    void adminApproveShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.adminApprove(delivery, true, 200.0, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MENGIRIM");
    }
}
