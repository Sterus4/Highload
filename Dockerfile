# Этап 1: Сборка с помощью Gradle
FROM gradle:8-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle .openapi-generator-java-sources.ignore ./
COPY gradle ./gradle

RUN --mount=type=cache,id=gradle-cache,target=/home/gradle/.gradle \
    gradle dependencies --no-daemon

COPY src ./src

RUN gradle openApiGenerate --no-daemon

RUN --mount=type=cache,id=gradle-cache,target=/home/gradle/.gradle \
    gradle build -x test --no-daemon


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/highload-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
