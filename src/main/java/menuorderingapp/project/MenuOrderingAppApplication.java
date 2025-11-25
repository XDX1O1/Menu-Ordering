package menuorderingapp.project;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class MenuOrderingAppApplication {

    @PostConstruct
    public void init() {
        // Set default timezone to Asia/Jakarta (WIB, UTC+7)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }

    public static void main(String[] args) {
        SpringApplication.run(MenuOrderingAppApplication.class, args);
    }
}
