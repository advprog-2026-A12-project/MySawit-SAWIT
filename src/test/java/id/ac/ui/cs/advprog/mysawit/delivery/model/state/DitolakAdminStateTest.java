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
    void getStatusNameShouldReturnDitolakAdmin() {
        assertThat(state.getStatusName()).isEqualTo("DITOLAK_ADMIN");
    }

    @Test
    void advanceStatusShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.advanceStatus(delivery))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }

    @Test
    void mandorApproveShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.mandorApprove(delivery, true, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }

    @Test
    void adminApproveShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.adminApprove(delivery, true, 200.0, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah final");
    }
}
