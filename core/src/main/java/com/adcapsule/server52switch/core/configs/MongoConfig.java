package com.adcapsule.server52switch.core.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@EnableMongoRepositories(basePackages = "com.adcapsule.server52switch.core.repositories")
public class MongoConfig extends AbstractMongoClientConfiguration {

    // Load the environment variables from the .env file
    private final Dotenv dotenv = Dotenv.load();

    @Override
    protected String getDatabaseName() {
        // Retrieve the database name from the .env file
        return dotenv.get("MONGODB_DATABASE");
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        // Create the MongoClient with the URI from the .env file
        return MongoClients.create(dotenv.get("MONGODB_URI"));
    }
}