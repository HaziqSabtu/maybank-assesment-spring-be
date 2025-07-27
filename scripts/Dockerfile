# ===== Stage 1: Build with Gradle and JDK 21 =====
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

# Build the Spring Boot app, skipping tests for speed
RUN gradle build -x test

# ===== Stage 2: Run with minimal JDK 21 runtime =====
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
