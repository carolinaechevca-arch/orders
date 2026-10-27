FROM gradle:9.2.1-jdk21-alpine AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon
COPY src src
RUN gradle bootJar --no-daemon
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=build /workspace/build/libs/notification-consumer-kafka-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
