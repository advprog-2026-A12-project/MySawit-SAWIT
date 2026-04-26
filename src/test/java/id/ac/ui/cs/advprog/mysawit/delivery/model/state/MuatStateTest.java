package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MuatStateTest {

    private MuatState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new MuatState();
        delivery = Delivery.builder()
                .status("MEMUAT")
                .payloadKg(200.0)
                .build();
    }

    @Test
    void getStatusName_shouldReturnMemuat() {
        assertThat(state.getStatusName()).isEqualTo("MEMUAT");
    }

    @Test
    void advanceStatus_shouldTransitionToMengirim() {
        state.advanceStatus(delivery);

        assertThat(delivery.getStatus()).isEqualTo("MENGIRIM");
        assertThat(delivery.getSentAt()).isNotNull();
    }

    @Test
    void mandorApprove_shouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.mandorApprove(delivery, true, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MEMUAT");
    }

    @Test
    void adminApprove_shouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.adminApprove(delivery, true, 200.0, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MEMUAT");
    }
}
