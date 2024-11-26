package team9.ddang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class DDangApplication {

    public static void main(String[] args) {
        SpringApplication.run(DDangApplication.class, args);
    }

}
