package com.jpmc.midascore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MidasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidasCoreApplication.class, args);
    }

    // Add this method below.
    // It allows @Autowired private RestTemplate restTemplate; to work in other classes.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}