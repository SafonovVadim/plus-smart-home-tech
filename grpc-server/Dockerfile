FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/grpc-server-1.0-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]