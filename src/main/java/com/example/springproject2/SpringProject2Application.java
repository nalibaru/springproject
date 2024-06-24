package com.example.springproject2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.example.model")
@SpringBootApplication(scanBasePackages = {"com.example"})
public class SpringProject2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringProject2Application.class, args);
    }

}
