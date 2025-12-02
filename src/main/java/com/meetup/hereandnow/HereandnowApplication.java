package com.meetup.hereandnow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class HereandnowApplication {

    public static void main(String[] args) {
        SpringApplication.run(HereandnowApplication.class, args);
    }

}
