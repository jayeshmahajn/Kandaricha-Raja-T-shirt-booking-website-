# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/tshirt-booking-1.0.0.jar app.jar

# Expose the port (Render uses PORT env variable)
EXPOSE 8081

# Run the jar
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8081}", "-jar", "app.jar"]
