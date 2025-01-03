# Set environment variables
$env:FIREBASE_AUTH_EMULATOR_HOST = "127.0.0.1:9099"
#$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\Users\ADCAPSULE\52switch\52switch-backend\src\main\resources\firebase\52switch_firebase_key.json"
$env:GOOGLE_APPLICATION_CREDENTIALS="classpath:firebase/52switch_firebase_key.json"
if (-Not (Test-Path "runner/.env")) {
    Copy-Item ".env" "runner/.env"
    Write-Host "The .env file was copied to runner/.env"
} else {
    Write-Host "runner/.env already exists."
}
# Run the Spring Boot application
mvn -f runner/pom.xml spring-boot:run