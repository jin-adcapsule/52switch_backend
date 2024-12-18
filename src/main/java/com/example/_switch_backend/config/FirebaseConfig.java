package com.example._switch_backend.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class FirebaseConfig {

    // Load environment variables using Dotenv
    private final Dotenv dotenv = Dotenv.load();

    @Bean
    public FirebaseApp initializeFirebase() {
        try {

            // Retrieve the Firebase credentials path from the .env file
            //String firebaseCredentialsPath = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
            /* 
            if (firebaseCredentialsPath == null || firebaseCredentialsPath.isEmpty()) {
                throw new IllegalArgumentException("GOOGLE_APPLICATION_CREDENTIALS is not set in the .env file");
            }

            // Load the service account JSON file
            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);
            */
            String credentialsPath = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
            System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + credentialsPath);
            System.out.println("Working Directory: " + new File(".").getAbsolutePath());
            InputStream serviceAccount = null;
            if (credentialsPath != null && credentialsPath.startsWith("classpath:")) {
                // Handle classpath-based paths
                credentialsPath = credentialsPath.replace("classpath:", "");
                serviceAccount = getClass().getClassLoader().getResourceAsStream(credentialsPath);
    
                if (serviceAccount == null) {
                    throw new FileNotFoundException("Firebase key file not found in classpath: " + credentialsPath);
                }
            } else if (credentialsPath != null) {
                // Handle absolute or relative paths
                serviceAccount = new FileInputStream(credentialsPath);
            } else {
                throw new IllegalArgumentException("GOOGLE_APPLICATION_CREDENTIALS environment variable is not set.");
            }
            // Configure Firebase options
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("switch-cf287") // Optional, specify the project ID
                .build();

            // Initialize and return the Firebase app
            return FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}