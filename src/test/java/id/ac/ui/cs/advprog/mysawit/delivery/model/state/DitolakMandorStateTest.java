package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DitolakMandorStateTest {

    private DitolakMandorState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new DitolakMandorState();
        delivery = Delivery.builder()
                .status("DITOLAK_MANDOR")
                .rejectionReason("Barang rusak")
                .build();
    }

    @Test
    void getStatusName_shouldReturnDitolakMandor() {
        assertThat(state.getStatusName()).isEqualTo("DITOLAK_MANDOR");
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
