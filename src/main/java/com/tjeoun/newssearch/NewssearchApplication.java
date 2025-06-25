package com.tjeoun.newssearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NewssearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewssearchApplication.class, args);
    }

}
