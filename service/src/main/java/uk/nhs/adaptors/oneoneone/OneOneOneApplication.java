package uk.nhs.adaptors.oneoneone;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OneOneOneApplication {
    public static void main(String[] args) {
        run(OneOneOneApplication.class);
    }
}
