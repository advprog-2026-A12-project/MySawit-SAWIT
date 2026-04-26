package id.ac.ui.cs.advprog.mysawit.delivery.service;

import id.ac.ui.cs.advprog.mysawit.delivery.dto.CreateDeliveryRequest;
import id.ac.ui.cs.advprog.mysawit.delivery.model.Delivery;
import id.ac.ui.cs.advprog.mysawit.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private UUID mandorId;
    private UUID supirId;
    private UUID harvestId;
    private UUID deliveryId;

    @BeforeEach
    void setUp() {
        mandorId = UUID.randomUUID();
        supirId = UUID.randomUUID();
        harvestId = UUID.randomUUID();
        deliveryId = UUID.randomUUID();
    }

    private CreateDeliveryRequest buildRequest(Double payload) {
        CreateDeliveryRequest req = new CreateDeliveryRequest();
        req.setSupirId(supirId);
        req.setSupirName("Andi Supir");
        req.setHarvestId(harvestId);
        req.setPayloadKg(payload);
        return req;
    }

    private Delivery buildDelivery(String status) {
        return Delivery.builder()
                .supirId(supirId)
                .mandorId(mandorId)
                .harvestId(harvestId)
                .payloadKg(200.0)
                .status(status)
                .build();
    }

    @Test
    void createDelivery_validPayload_shouldSaveAndReturnDelivery() {
        CreateDeliveryRequest req = buildRequest(200.0);
        Delivery saved = buildDelivery("MEMUAT");
        when(deliveryRepository.save(any())).thenReturn(saved);

        Delivery result = deliveryService.createDelivery(req, mandorId, "Budi Mandor");

        assertThat(result.getStatus()).isEqualTo("MEMUAT");
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void createDelivery_payloadTooHigh_shouldThrowIllegalArgument() {
        CreateDeliveryRequest req = buildRequest(401.0);

        assertThatThrownBy(() -> deliveryService.createDelivery(req, mandorId, "Budi"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("400");
    }

    @Test
    void createDelivery_payloadTooLow_shouldThrowIllegalArgument() {
        CreateDeliveryRequest req = buildRequest(0.0);

        assertThatThrownBy(() -> deliveryService.createDelivery(req, mandorId, "Budi"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1");
    }

    @Test
    void createDelivery_nullPayload_shouldThrowIllegalArgument() {
        CreateDeliveryRequest req = buildRequest(null);

        assertThatThrownBy(() -> deliveryService.createDelivery(req, mandorId, "Budi"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getDeliveriesByRole_admin_shouldReturnAll() {
        List<Delivery> all = List.of(buildDelivery("MEMUAT"), buildDelivery("MENGIRIM"));
        when(deliveryRepository.findAll()).thenReturn(all);

        List<Delivery> result = deliveryService.getDeliveriesByRole(mandorId, "ADMIN");

        assertThat(result).hasSize(2);
    }

    @Test
    void getDeliveriesByRole_mandor_shouldReturnByMandorId() {
        List<Delivery> mandorDeliveries = List.of(buildDelivery("MEMUAT"));
        when(deliveryRepository.findByMandorId(mandorId)).thenReturn(mandorDeliveries);

        List<Delivery> result = deliveryService.getDeliveriesByRole(mandorId, "MANDOR");

        assertThat(result).hasSize(1);
    }

    @Test
    void getDeliveriesByRole_supir_shouldReturnBySupirId() {
        List<Delivery> supirDeliveries = List.of(buildDelivery("MENGIRIM"));
        when(deliveryRepository.findBySupirId(supirId)).thenReturn(supirDeliveries);

        List<Delivery> result = deliveryService.getDeliveriesByRole(supirId, "SUPIR_TRUK");

        assertThat(result).hasSize(1);
    }

    @Test
    void getDeliveriesBySupirId_shouldDelegateToRepository() {
        List<Delivery> list = List.of(buildDelivery("MEMUAT"));
        when(deliveryRepository.findBySupirId(supirId)).thenReturn(list);

        List<Delivery> result = deliveryService.getDeliveriesBySupirId(supirId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getDeliveriesByMandorFiltered_withName_shouldFilterByName() {
        List<Delivery> filtered = List.of(buildDelivery("MEMUAT"));
        when(deliveryRepository.findByMandorIdAndSupirNameContainingIgnoreCase(mandorId, "Andi"))
                .thenReturn(filtered);

        List<Delivery> result = deliveryService.getDeliveriesByMandorFiltered(mandorId, "Andi");

        assertThat(result).hasSize(1);
    }

    @Test
    void getDeliveriesByMandorFiltered_withBlankName_shouldReturnAll() {
        List<Delivery> all = List.of(buildDelivery("MEMUAT"), buildDelivery("MENGIRIM"));
        when(deliveryRepository.findByMandorId(mandorId)).thenReturn(all);

        List<Delivery> result = deliveryService.getDeliveriesByMandorFiltered(mandorId, "  ");

        assertThat(result).hasSize(2);
    }

    @Test
    void getDeliveriesByMandorFiltered_withNullName_shouldReturnAll() {
        List<Delivery> all = List.of(buildDelivery("MEMUAT"));
        when(deliveryRepository.findByMandorId(mandorId)).thenReturn(all);

        List<Delivery> result = deliveryService.getDeliveriesByMandorFiltered(mandorId, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void advanceStatus_fromMemuat_shouldTransitionToMengirim() {
        Delivery delivery = buildDelivery("MEMUAT");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.advanceStatus(deliveryId);

        assertThat(result.getStatus()).isEqualTo("MENGIRIM");
        assertThat(result.getSentAt()).isNotNull();
    }

    @Test
    void advanceStatus_fromMengirim_shouldTransitionToTibaDiTujuan() {
        Delivery delivery = buildDelivery("MENGIRIM");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.advanceStatus(deliveryId);

        assertThat(result.getStatus()).isEqualTo("TIBA_DI_TUJUAN");
        assertThat(result.getArrivedAt()).isNotNull();
    }

    @Test
    void advanceStatus_fromSelesai_shouldThrowIllegalState() {
        Delivery delivery = buildDelivery("SELESAI");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.advanceStatus(deliveryId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void advanceStatus_notFound_shouldThrowRuntime() {
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.advanceStatus(deliveryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    void mandorApprove_approve_fromTibaDiTujuan_shouldBeDisetujuiMandor() {
        Delivery delivery = buildDelivery("TIBA_DI_TUJUAN");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.mandorApprove(deliveryId, true, null);

        assertThat(result.getStatus()).isEqualTo("DISETUJUI_MANDOR");
    }

    @Test
    void mandorApprove_reject_fromTibaDiTujuan_shouldBeDitolakMandor() {
        Delivery delivery = buildDelivery("TIBA_DI_TUJUAN");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.mandorApprove(deliveryId, false, "Barang rusak");

        assertThat(result.getStatus()).isEqualTo("DITOLAK_MANDOR");
        assertThat(result.getRejectionReason()).isEqualTo("Barang rusak");
    }

    @Test
    void mandorApprove_fromMemuat_shouldThrowIllegalState() {
        Delivery delivery = buildDelivery("MEMUAT");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.mandorApprove(deliveryId, true, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void mandorApprove_notFound_shouldThrowRuntime() {
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.mandorApprove(deliveryId, true, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    void adminApprove_approve_fromDisetujuiMandor_shouldBeSelesai() {
        Delivery delivery = buildDelivery("DISETUJUI_MANDOR");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.adminApprove(deliveryId, true, 180.0, null);

        assertThat(result.getStatus()).isEqualTo("SELESAI");
        assertThat(result.getApprovedPayloadKg()).isEqualTo(180.0);
    }

    @Test
    void adminApprove_reject_fromDisetujuiMandor_shouldBeDitolakAdmin() {
        Delivery delivery = buildDelivery("DISETUJUI_MANDOR");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Delivery result = deliveryService.adminApprove(deliveryId, false, null, "Dokumen kurang");

        assertThat(result.getStatus()).isEqualTo("DITOLAK_ADMIN");
        assertThat(result.getRejectionReason()).isEqualTo("Dokumen kurang");
    }

    @Test
    void adminApprove_fromMengirim_shouldThrowIllegalState() {
        Delivery delivery = buildDelivery("MENGIRIM");
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.adminApprove(deliveryId, true, 180.0, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void adminApprove_notFound_shouldThrowRuntime() {
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.adminApprove(deliveryId, true, 180.0, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }
}
