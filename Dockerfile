FROM gradle:8.6.0-jdk21 AS gradlebuilder
WORKDIR /home
COPY . /home
RUN gradle build

FROM amazoncorretto:21-alpine-full
COPY --from=gradlebuilder home/build/libs/*.jar tesch_discord_bot-1.0.jar
ENTRYPOINT ["java", "-jar", "tesch_discord_bot-1.0.jar"]