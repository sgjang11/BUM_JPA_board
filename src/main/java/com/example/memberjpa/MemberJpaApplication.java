package com.example.memberjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MemberJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberJpaApplication.class, args);
    }

}
