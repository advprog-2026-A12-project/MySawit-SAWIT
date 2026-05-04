package id.ac.ui.cs.advprog.mysawit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MysawitApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysawitApplication.class, args);
    }

}
