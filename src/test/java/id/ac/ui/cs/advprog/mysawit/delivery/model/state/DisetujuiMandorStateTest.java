package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DisetujuiMandorStateTest {

    private DisetujuiMandorState state;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        state = new DisetujuiMandorState();
        delivery = Delivery.builder()
                .status("DISETUJUI_MANDOR")
                .payloadKg(200.0)
                .build();
    }

    @Test
    void getStatusNameShouldReturnDisetujuiMandor() {
        assertThat(state.getStatusName()).isEqualTo("DISETUJUI_MANDOR");
    }

    @Test
    void advanceStatusShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.advanceStatus(delivery))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void mandorApproveShouldThrowIllegalStateException() {
        assertThatThrownBy(() -> state.mandorApprove(delivery, true, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void adminApproveApprovedWithCustomPayloadShouldBeSelesai() {
        state.adminApprove(delivery, true, 180.0, null);
        assertThat(delivery.getStatus()).isEqualTo("SELESAI");
        assertThat(delivery.getApprovedPayloadKg()).isEqualTo(180.0);
        assertThat(delivery.getRejectionReason()).isNull();
    }

    @Test
    void adminApproveApprovedWithNullPayloadShouldFallbackToPayloadKg() {
        delivery.setPayloadKg(200.0);
        state.adminApprove(delivery, true, null, null);
        assertThat(delivery.getStatus()).isEqualTo("SELESAI");
        assertThat(delivery.getApprovedPayloadKg()).isEqualTo(200.0);
    }

    @Test
    void adminApproveRejectedShouldBeDitolakAdmin() {
        state.adminApprove(delivery, false, null, "Dokumen tidak lengkap");
        assertThat(delivery.getStatus()).isEqualTo("DITOLAK_ADMIN");
        assertThat(delivery.getRejectionReason()).isEqualTo("Dokumen tidak lengkap");
    }
}
