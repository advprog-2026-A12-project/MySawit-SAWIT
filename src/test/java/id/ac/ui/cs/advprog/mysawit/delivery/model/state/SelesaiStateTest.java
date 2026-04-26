package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelesaiStateTest {

    private SelesaiState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new SelesaiState();
        delivery = Delivery.builder()
                .status("SELESAI")
                .approvedPayloadKg(200.0)
                .build();
    }

    @Test
    void getStatusName_shouldReturnSelesai() {
        assertThat(state.getStatusName()).isEqualTo("SELESAI");
    }

    @Test
    void advanceStatus_shouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.advanceStatus(delivery))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }

    @Test
    void mandorApprove_shouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.mandorApprove(delivery, true, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }

    @Test
    void adminApprove_shouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.adminApprove(delivery, true, 200.0, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }
}
