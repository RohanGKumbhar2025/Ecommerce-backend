# =====================================================================================
# Stage 1: Build the application using Maven
# This stage uses a full JDK and Maven to compile the code and create the JAR file.
# =====================================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set the working directory
WORKDIR /build

# Copy the pom.xml and download dependencies first to leverage caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping the tests for a faster build
RUN mvn clean package -DskipTests

# =====================================================================================
# Stage 2: Create the final, lightweight production image
# This stage uses a smaller, more secure JRE.
# =====================================================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a dedicated, unprivileged user for security
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copy ONLY the built JAR file from the 'builder' stage
COPY --from=builder /build/target/productcatalog-0.0.1-SNAPSHOT.jar app.jar

# Change ownership of the app directory to the new user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]