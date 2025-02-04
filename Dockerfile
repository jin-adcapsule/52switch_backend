# Use official OpenJDK base image
FROM openjdk:17-slim

# Install curl (and optionally ping for debugging)
RUN apt update && apt install -y curl iputils-ping
# Install CA Certificates (needed for MongoDB Atlas SSL)
RUN apt-get update && apt-get install -y ca-certificates && update-ca-certificates

# Set working directory
WORKDIR /app

# Copy JAR files from the build
COPY runner/target/*.jar runner.jar
COPY backoffice/target/*.jar backoffice.jar
COPY core/target/*.jar core.jar
COPY shared/target/*.jar shared.jar

# Expose the application port
EXPOSE 8080

# Run the main application (adjust as needed)!
CMD ["java", "-Dhttps.protocols=TLSv1.2", "-jar", "runner.jar"]

