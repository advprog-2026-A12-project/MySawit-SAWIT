package id.ac.ui.cs.advprog.mysawit.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "SAWIT SERVICE RUNNING";
    }
}