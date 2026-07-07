FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/config-server-1.0-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]