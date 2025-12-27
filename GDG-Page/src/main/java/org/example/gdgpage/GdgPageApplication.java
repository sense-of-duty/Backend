package org.example.gdgpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GdgPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(GdgPageApplication.class, args);
    }

}
