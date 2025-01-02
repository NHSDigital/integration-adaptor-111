FROM gradle:7.6-jdk17 AS build
COPY --chown=gradle:gradle service /home/gradle/service

WORKDIR /home/gradle/service

RUN ./gradlew --build-cache bootJar

FROM eclipse-temurin:17-jre-jammy

EXPOSE 8081

RUN mkdir /app

COPY --from=build /home/gradle/service/build/libs/integration-adaptor-111.jar /app/integration-adaptor-111.jar

ENTRYPOINT ["java", "-cp", "/app/integration-adaptor-111.jar", "-Dloader.main=uk.nhs.adaptors.oneoneone.OneOneOneApplication", "org.springframework.boot.loader.PropertiesLauncher"]
