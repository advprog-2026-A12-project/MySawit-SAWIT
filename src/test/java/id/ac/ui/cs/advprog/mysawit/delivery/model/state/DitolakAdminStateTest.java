package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DitolakAdminStateTest {

    private DitolakAdminState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new DitolakAdminState();
        delivery = Delivery.builder()
                .status("DITOLAK_ADMIN")
                .rejectionReason("Dokumen tidak lengkap")
                .build();
    }

    @Test
    void getStatusName_shouldReturnDitolakAdmin() {
        assertThat(state.getStatusName()).isEqualTo("DITOLAK_ADMIN");
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
