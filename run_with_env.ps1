# Set environment variables
$env:FIREBASE_AUTH_EMULATOR_HOST = "127.0.0.1:9099"
#$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\Users\ADCAPSULE\52switch\52switch-backend\src\main\resources\firebase\52switch_firebase_key.json"
$env:GOOGLE_APPLICATION_CREDENTIALS="classpath:firebase/52switch_firebase_key.json"
# Run the Spring Boot application
mvn spring-boot:run