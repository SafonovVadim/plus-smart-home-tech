FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/collector-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]