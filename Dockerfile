FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew

COPY src src

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache curl

WORKDIR /app

RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-XX:+UseContainerSupport", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}", \
    "-jar", "app.jar"] 