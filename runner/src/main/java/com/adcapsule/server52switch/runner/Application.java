package com.adcapsule.server52switch.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication(scanBasePackages = "com.adcapsule.server52switch")
//@EnableMongoRepositories(basePackages = "com.adcapsule.server52switch.core.repositories")  // Enable scanning for MongoDB repositories
@SpringBootApplication(scanBasePackages = "com.adcapsule.server52switch")
@ComponentScan(basePackages = "com.adcapsule.server52switch.core.configs")
//@EnableMongoRepositories(basePackages = "com.adcapsule.server52switch.core.repositories")  // Enable scanning for MongoDB repositories
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}