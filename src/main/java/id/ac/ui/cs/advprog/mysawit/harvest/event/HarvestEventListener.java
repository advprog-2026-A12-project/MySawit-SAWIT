package id.ac.ui.cs.advprog.mysawit.harvest.listener;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.PayrollInternalRequest;
import id.ac.ui.cs.advprog.mysawit.harvest.event.HarvestApprovedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HarvestEventListener {

    private final RestTemplate restTemplate = new RestTemplate();

    // Base URL Payment
    @Value("${payment.service.url:https://mysawit-payment-2df96a73ee96.herokuapp.com/api/v1}")
    private String paymentUrl;

    // Key
    @Value("${internal.api.key:1a2e714c3d800ec61327d9a8902b44042bb30b9bbcdac59510a953b87f0eadf1}")
    private String internalApiKey;

    @Async
    @EventListener
    public void handleHarvestApproved(HarvestApprovedEvent event) {
        // 1. Bungkus data sesuai kontrak Modul 5
        PayrollInternalRequest request = new PayrollInternalRequest(
                event.buruhId(),       // userId
                "BURUH",               // userRole
                "HARVEST",             // referenceType
                event.harvestId(),     // referenceId
                event.kilogram()       // kilogram
        );

        // Headers untuk otentikasi internal
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Api-Key", internalApiKey);

        // Gabungkan Header dan Body
        HttpEntity<PayrollInternalRequest> entity = new HttpEntity<>(request, headers);

        try {
            // 4. Tembak ke endpoint /internal/payrolls Modul 5
            String endpoint = paymentUrl + "/internal/payrolls";
            restTemplate.postForEntity(endpoint, entity, String.class);

            System.out.println("✅ Sukses kirim data payroll ke Payment. HarvestID: " + event.harvestId());
        } catch (Exception e) {
            System.err.println("❌ Gagal kirim payroll ke Payment: " + e.getMessage());
        }
    }
}