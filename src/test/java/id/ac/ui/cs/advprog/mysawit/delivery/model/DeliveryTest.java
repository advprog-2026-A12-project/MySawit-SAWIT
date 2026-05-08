package id.ac.ui.cs.advprog.mysawit.delivery.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryTest {

    private Delivery buildDefault() {
        return Delivery.builder()
                .supirId(UUID.randomUUID())
                .supirName("Andi Supir")
                .mandorId(UUID.randomUUID())
                .mandorName("Budi Mandor")
                .harvestIds(List.of(UUID.randomUUID()))
                .payloadKg(200.0)
                .build();
    }

    @Test
    void defaultStatusShouldBeMemuat() {
        Delivery delivery = buildDefault();
        assertThat(delivery.getStatus()).isEqualTo("MEMUAT");
    }

    @Test
    void builderShouldSetAllFields() {
        UUID supirId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        UUID harvestId = UUID.randomUUID();

        Delivery delivery = Delivery.builder()
                .supirId(supirId)
                .supirName("Supir A")
                .mandorId(mandorId)
                .mandorName("Mandor B")
                .harvestIds(List.of(harvestId))
                .payloadKg(350.0)
                .approvedPayloadKg(300.0)
                .status("MENGIRIM")
                .rejectionReason("Reason X")
                .build();

        assertThat(delivery.getSupirId()).isEqualTo(supirId);
        assertThat(delivery.getSupirName()).isEqualTo("Supir A");
        assertThat(delivery.getMandorId()).isEqualTo(mandorId);
        assertThat(delivery.getMandorName()).isEqualTo("Mandor B");
        assertThat(delivery.getHarvestIds()).containsExactly(harvestId);
        assertThat(delivery.getPayloadKg()).isEqualTo(350.0);
        assertThat(delivery.getApprovedPayloadKg()).isEqualTo(300.0);
        assertThat(delivery.getStatus()).isEqualTo("MENGIRIM");
        assertThat(delivery.getRejectionReason()).isEqualTo("Reason X");
    }

    @Test
    void setterShouldUpdateFields() {
        Delivery delivery = buildDefault();

        delivery.setStatus("TIBA_DI_TUJUAN");
        delivery.setRejectionReason("Barang kurang");
        delivery.setApprovedPayloadKg(180.0);

        LocalDateTime now = LocalDateTime.now();
        delivery.setSentAt(now);
        delivery.setArrivedAt(now);

        assertThat(delivery.getStatus()).isEqualTo("TIBA_DI_TUJUAN");
        assertThat(delivery.getRejectionReason()).isEqualTo("Barang kurang");
        assertThat(delivery.getApprovedPayloadKg()).isEqualTo(180.0);
        assertThat(delivery.getSentAt()).isEqualTo(now);
        assertThat(delivery.getArrivedAt()).isEqualTo(now);
    }

    @Test
    void noArgsConstructorShouldWork() {
        Delivery delivery = new Delivery();
        assertThat(delivery).isNotNull();
        assertThat(delivery.getId()).isNull();
        assertThat(delivery.getPayloadKg()).isNull();
    }
}
