# Set environment variables
$env:FIREBASE_AUTH_EMULATOR_HOST = "127.0.0.1:9099"
#$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\Users\ADCAPSULE\52switch\52switch-backend\src\main\resources\firebase\52switch_firebase_key.json"
$env:GOOGLE_APPLICATION_CREDENTIALS="classpath:firebase/52switch_firebase_key.json"
# Overwrite the .env file in the runner folder
$sourceEnvPath = ".env"
$destinationEnvPath = "runner/.env"

# Check if the source .env file exists
if (Test-Path $sourceEnvPath) {
    # Check if destination .env file exists and delete it if it does
    if (Test-Path $destinationEnvPath) {
        Remove-Item $destinationEnvPath -Force
        Write-Host "Existing .env file in destination was deleted."
    }

    # Now copy the new .env file from source to destination
    Copy-Item $sourceEnvPath $destinationEnvPath
    Write-Host "The .env file was copied to $destinationEnvPath."
} else {
    Write-Host "Source .env file not found at $sourceEnvPath."
}
# Check if Firebase key exists
#$firebaseKeyPath = "shared/module/src/main/resources/firebase/52switch_firebase_key.json"
if (-Not (Test-Path $env:GOOGLE_APPLICATION_CREDENTIALS)) {
    Write-Host "Firebase key not found. Running flutterfire configure..."
    # Replace 'switch-cf287' with your actual Firebase project ID
    flutterfire configure --project=switch-cf287
} else {
    Write-Host "Firebase key already exists at $env:GOOGLE_APPLICATION_CREDENTIALS"
}
# Run the Spring Boot application
mvn -f runner/pom.xml spring-boot:run