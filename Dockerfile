# Use lightweight JDK image
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy your built jar file into container
COPY target/productcatalog-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 8081

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
