FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# The dev Maven profile packages only the Java backend; the runtime activates prod.
RUN mvn -B -DskipTests -Pdev package spring-boot:repackage

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /data/videos
COPY --from=build /app/target/protube-back-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
