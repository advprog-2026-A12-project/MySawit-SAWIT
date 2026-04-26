package id.ac.ui.cs.advprog.mysawit.delivery.model.state;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryStateFactoryTest {

    @Test
    void getState_memuat_shouldReturnMuatState() {
        DeliveryState state = DeliveryStateFactory.getState("MEMUAT");
        assertThat(state).isInstanceOf(MuatState.class);
    }

    @Test
    void getState_mengirim_shouldReturnMengirimState() {
        DeliveryState state = DeliveryStateFactory.getState("MENGIRIM");
        assertThat(state).isInstanceOf(MengirimState.class);
    }

    @Test
    void getState_tibaDiTujuan_shouldReturnTibaDiTujuanState() {
        DeliveryState state = DeliveryStateFactory.getState("TIBA_DI_TUJUAN");
        assertThat(state).isInstanceOf(TibaDiTujuanState.class);
    }

    @Test
    void getState_disetujuiMandor_shouldReturnDisetujuiMandorState() {
        DeliveryState state = DeliveryStateFactory.getState("DISETUJUI_MANDOR");
        assertThat(state).isInstanceOf(DisetujuiMandorState.class);
    }

    @Test
    void getState_ditolakMandor_shouldReturnDitolakMandorState() {
        DeliveryState state = DeliveryStateFactory.getState("DITOLAK_MANDOR");
        assertThat(state).isInstanceOf(DitolakMandorState.class);
    }

    @Test
    void getState_selesai_shouldReturnSelesaiState() {
        DeliveryState state = DeliveryStateFactory.getState("SELESAI");
        assertThat(state).isInstanceOf(SelesaiState.class);
    }

    @Test
    void getState_ditolakAdmin_shouldReturnDitolakAdminState() {
        DeliveryState state = DeliveryStateFactory.getState("DITOLAK_ADMIN");
        assertThat(state).isInstanceOf(DitolakAdminState.class);
    }

    @Test
    void getState_unknown_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> DeliveryStateFactory.getState("STATUS_TIDAK_ADA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("STATUS_TIDAK_ADA");
    }

    @Test
    void getState_null_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> DeliveryStateFactory.getState(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }
}
