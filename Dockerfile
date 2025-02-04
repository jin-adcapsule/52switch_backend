# Use official OpenJDK base image
FROM openjdk:17
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
CMD ["java", "-jar", "runner.jar"]

