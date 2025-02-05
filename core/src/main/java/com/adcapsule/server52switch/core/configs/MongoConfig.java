package com.adcapsule.server52switch.core.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

//import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    // Load the environment variables from the .env file
    //private final Dotenv dotenv = Dotenv.load();

    @Override
    protected String getDatabaseName() {
        // Retrieve the database name from the .env file
        return System.getenv("MONGODB_DATABASE");
        //return dotenv.get("MONGODB_DATABASE");
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        // Create the MongoClient with the URI from the .env file
        //return MongoClients.create(dotenv.get("MONGODB_URI"));
        String mongoUri = System.getenv("MONGODB_URI");
        String databaseName = getDatabaseName();
        
        if (mongoUri == null || mongoUri.isEmpty()) {
            throw new IllegalStateException("MONGODB_URI environment variable is not set.");
        }
        if (databaseName == null || databaseName.isEmpty()) {
            throw new IllegalStateException("MONGODB_DATABASE environment variable is not set.");
        }

        // Ensure the MongoDB URI includes the database name
        if (!mongoUri.endsWith("/")) {
            mongoUri += "/";
        }
        mongoUri += databaseName;

        // Ensure TLS and retry options are set
        if (!mongoUri.contains("?")) {
            mongoUri += "?retryWrites=true&w=majority&tls=true";
        } else {
            mongoUri += "&retryWrites=true&w=majority&tls=true";
        }

        return MongoClients.create(mongoUri);
    }
}