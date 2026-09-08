# Stage 1: Build stage with Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first to take advantage of Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime image with Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root system user for security
RUN addgroup -S transittrack && adduser -S transittrack -G transittrack
USER transittrack

# Copy the built artifact from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose default application port
EXPOSE 8080

# Configure JVM memory limits and entrypoint
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
