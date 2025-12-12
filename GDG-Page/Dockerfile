# 1단계: 빌드
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# 2단계: 런타임
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ENV TZ=Asia/Seoul
ENV LANG=C.UTF-8

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
