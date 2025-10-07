package com.meetup.hereandnow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LoverouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoverouteApplication.class, args);
    }

}
