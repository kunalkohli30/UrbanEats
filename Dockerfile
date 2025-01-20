# Use the official OpenJDK image as a parent image
FROM openjdk:18-jdk-alpine

# Set the maintainer label
LABEL maintainer="KUNALKOHLI"

# Copy the Spring Boot application's jar file to the container
COPY target/UrbanEats-0.0.1-SNAPSHOT.jar urban-eats-backend-server-0.0.1.jar

# Expose the port that the application will run on
EXPOSE 9000

# Set the entry point to run the jar file
ENTRYPOINT ["java", "-jar", "/urban-eats-backend-server-0.0.1.jar"]