FROM gradle:jdk11 AS build
COPY --chown=gradle:gradle ./service /home/gradle/service
WORKDIR /home/gradle/service
RUN ./gradlew test --build-cache bootJar

FROM adoptopenjdk/openjdk11-openj9:jre

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/service/build/libs/integration-adaptor-111.jar /app/integration-adaptor-111.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/integration-adaptor-111.jar"]
