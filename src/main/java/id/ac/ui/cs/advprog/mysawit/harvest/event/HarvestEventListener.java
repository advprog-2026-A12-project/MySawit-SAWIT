package id.ac.ui.cs.advprog.mysawit.harvest.event;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.PaymentTriggerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HarvestEventListener {

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    @EventListener
    public void handleHarvestApproved(HarvestApprovedEvent event) {
        PaymentTriggerRequest request = new PaymentTriggerRequest(
                event.harvestId(),
                event.buruhId(),
                event.kilogram()
        );
        try {
            // URL modul 5
            String paymentUrl = "http://localhost:8085/api/payment/trigger"; //replace nntiy

            restTemplate.postForEntity(paymentUrl, request, String.class);
            System.out.println("Sukses ngirim data ke Modul 5!");
        } catch (Exception e) {
            System.err.println("Gagal ngirim ke Modul 5: " + e.getMessage());
        }
    }
}