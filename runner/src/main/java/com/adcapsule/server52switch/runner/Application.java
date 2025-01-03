package com.adcapsule.server52switch.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//@SpringBootApplication(scanBasePackages = "com.adcapsule.server52switch")
//@EnableMongoRepositories(basePackages = "com.adcapsule.server52switch.core.repositories")  // Enable scanning for MongoDB repositories
@SpringBootApplication()
@EnableMongoRepositories(basePackages = "com.adcapsule.server52switch.core.repositories")  // Enable scanning for MongoDB repositories
@ComponentScan(basePackages = {
    "com.adcapsule.server52switch.core",
    "com.adcapsule.server52switch.shared",
    "com.adcapsule.server52switch.backoffice",
    "com.adcapsule.server52switch.runner"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}