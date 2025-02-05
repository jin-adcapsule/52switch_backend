# Use official OpenJDK base image
FROM openjdk:17-jdk-slim

# Install necessary packages for SSL, DNS, and debugging
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    iputils-ping \
    dnsutils \
    netcat \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*
# Set working directorye
WORKDIR /app

# Copy JAR files from the build
COPY runner/target/*.jar runner.jar
COPY backoffice/target/*.jar backoffice.jar
COPY core/target/*.jar core.jar
COPY shared/target/*.jar shared.jar

# Expose the application port
EXPOSE 8080


# Set MongoDB SSL configurations and DNS settings
ENV JAVA_OPTS="\
    -Djava.security.egd=file:/dev/./urandom \
    -Dhttps.protocols=TLSv1.2 \
    -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts \
    -Djavax.net.ssl.trustStorePassword=changeit \
    -Djava.net.preferIPv4Stack=true \
    -Dnetworkaddress.cache.ttl=60"

# Run the main application with updated configurations
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar runner.jar"]
